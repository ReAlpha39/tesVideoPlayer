package id.nyaa.tesvideoplayerii.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class LocalVideoBean implements Parcelable {
    public String title;//Video name
    public String duration;//Video duration
    public long size;//Video size
    public String path;//Video path

    public LocalVideoBean(String title, String duration, long size, String path) {
        this.title = title;
        this.duration = duration;
        this.size = size;
        this.path = path;
    }

    public static final Creator<LocalVideoBean> CREATOR  =  new Creator<LocalVideoBean>() {
        @Override
        public LocalVideoBean createFromParcel(Parcel source) {
            LocalVideoBean video = new LocalVideoBean(source.readString(),source.readString(),source.readLong(),source.readString());
            return video;
        }

        @Override
        public LocalVideoBean[] newArray(int size) {
            return new LocalVideoBean[size];
        }
    };

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(duration);
        dest.writeLong(size);
        dest.writeString(path);
    }
}

