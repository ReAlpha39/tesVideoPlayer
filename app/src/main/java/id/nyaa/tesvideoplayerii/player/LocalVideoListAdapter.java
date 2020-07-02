package id.nyaa.tesvideoplayerii.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.nyaa.tesvideoplayerii.R;
import id.nyaa.tesvideoplayerii.bean.LocalVideoBean;
import id.nyaa.tesvideoplayerii.util.StringUtils;

public class LocalVideoListAdapter extends BaseAdapter {

    private List<LocalVideoBean> mVideoList;
    private Context mContext;
    private LocalVideoImageLoader mVideoImageLoader;


    public LocalVideoListAdapter(List<LocalVideoBean> videoList, Context context) {
        super();
        this.mVideoList = videoList;
        this.mContext = context;
        // Initialize thumbnail loading method
        mVideoImageLoader = new LocalVideoImageLoader(context);
    }

    @Override
    public int getCount() {
        return mVideoList.size();
    }

    @Override
    public LocalVideoBean getItem(int position) {
        return mVideoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_local_video_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.setData(mVideoList.get(position));
        viewHolder.image.setTag(mVideoImageLoader.hashKeyForDisk(mVideoList.get(position).path));
        mVideoImageLoader.showThumbByAsynctack(mVideoList.get(position).path, viewHolder.image);
        return convertView;
    }

    private static class ViewHolder{
        private ImageView image;
        private TextView title;
        private TextView duration;
        Bitmap bitmap = Bitmap.createBitmap( 10,10, Bitmap.Config.ARGB_4444 );

        public ViewHolder(View view){
            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
            duration = view.findViewById(R.id.duration);
        }

        public void setData(LocalVideoBean video){
            title.setText(video.title);
            duration.setText(StringUtils.generateTime(Long.parseLong(video.duration)));
            image.setImageBitmap(bitmap);
        }
    }
}

