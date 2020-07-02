package id.nyaa.tesvideoplayerii.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Resources implements Parcelable {
    public int type = 1;
    public String title;
    public String link;
    public String duration;
    public String description;
    public Resources() {
    }

    private Resources(Parcel in) {
        type = in.readInt();
        title = in.readString();
        link = in.readString();
        duration = in.readString();
        description = in.readString();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public static final Creator<Resources> CREATOR = new Creator<Resources>() {
        @Override
        public Resources createFromParcel(Parcel in) {
            return new Resources(in);
        }

        @Override
        public Resources[] newArray(int size) {
            return new Resources[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeString(duration);
        parcel.writeString(description);
    }
}
