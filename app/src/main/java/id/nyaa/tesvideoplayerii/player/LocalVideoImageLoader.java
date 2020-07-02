package id.nyaa.tesvideoplayerii.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import id.nyaa.tesvideoplayerii.R;

public class LocalVideoImageLoader {
    private Context mContext;
    private String key;
    //Create cache
    private LruCache<String, Bitmap> lruCache;

    /**
     * Picture cache
     */
    private DiskLruCache mDiskLruCache;

    public LocalVideoImageLoader(Context context){
        this.mContext = context;
        //Initialize LruCache memory cache
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//Get the maximum running memory
        int maxSize = maxMemory / 8; //Get the cached memory size 35
        lruCache = new LruCache<String, Bitmap>(maxSize){
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                //This method will be called every time it is stored in the cache
                return value.getByteCount();
            }
        };

        //Initialize DiskCache local cache
        try {
            // Get image cache path
            File cacheDir = getDiskCacheDir(mContext, "VideoThumb");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            // Create DiskLruCache instance, initialize cache data
            mDiskLruCache = DiskLruCache
                    .open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showThumbByAsynctack(String path, ImageView imgview){
        key = hashKeyForDisk(path);
        if(getVideoThumbToCache(key) == null){
            //Memory load failed, load or generate video screenshot from local
            new MyBobAsynctack(imgview, path).execute(path);
        }else{
            imgview.setImageBitmap(getVideoThumbToCache(key));
        }

    }

    public void addVideoThumbToCache(String key,Bitmap bitmap){

        if(getVideoThumbToCache(key) == null){
            //When the current address is not cached, add
            lruCache.put(key, bitmap);
        }
    }

    public Bitmap getVideoThumbToCache(String key){

        return lruCache.get(key);

    }

    class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        private String path;

        public MyBobAsynctack(ImageView imageView,String path) {
            this.imgView = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapShot = null;
            try {
                // The key corresponding to the image URL is generated.
                final String key = hashKeyForDisk(path);
                // Find the cache corresponding to the key
                snapShot = mDiskLruCache.get(key);
                if (snapShot == null) {
                    // If no corresponding local cache is found,
                    // prepare to request data from the network
                    // and/or generate video screenshots and write
                    // to the local cache and memory cache
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (downloadUrlToStream(path, outputStream)) {
                            System.out.println("downloadUrlToStream");
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                    }
                    // After the cache is written, search the cache corresponding to the key again
                    snapShot = mDiskLruCache.get(key);
                }
                if (snapShot != null) {
                    fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }
                // Parse cached data into Bitmap objects
                Bitmap bitmap = null;
                if (fileDescriptor != null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                }
                if (bitmap != null) {
                    // Add Bitmap object to memory cache
                    addVideoThumbToCache(key, bitmap);
                }
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileDescriptor == null && fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(imgView.getTag().equals(hashKeyForDisk(path))){
                //You can bind the image address and imageView through Tag,
                // which is one of the solutions to solve the misplacement of
                // loading images in Listview
                if(bitmap != null){
                    imgView.setImageBitmap(bitmap);
                }else{
                    imgView.setImageResource(R.mipmap.video_fail);
                }
            }
        }
    }

    /**
     * Obtain the path address of the disk
     * cache based on the uniqueName passed in.
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Get the version number of the current application.
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Use the MD5 algorithm to encrypt the incoming key and return it.
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Create an HTTP request and get the Bitmap object.
     *
     *            URL of the picture
     * @return Parsed Bitmap object
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(urlString,  MediaStore.Video.Thumbnails.MINI_KIND);
        if(bitmap != null){
            ByteArrayOutputStream baos = null;
            InputStream inputStream = null;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            try {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                inputStream = new ByteArrayInputStream(baos.toByteArray());
                in = new BufferedInputStream(inputStream, 8 * 1024);
                out = new BufferedOutputStream(outputStream, 8 * 1024);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                return true;
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (baos != null) {
                        baos.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        return false;
    }

}
