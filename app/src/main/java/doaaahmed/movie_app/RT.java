package doaaahmed.movie_app;

import android.os.Parcel;
import android.os.Parcelable;


public class RT implements Parcelable{

    boolean trailer;
    String RTtitle;
    String content;

    public boolean isTrailer() {
        return trailer;
    }

    public void setTrailer(boolean trailer) {
        this.trailer = trailer;
    }

    public String getRTtitle() {
        return RTtitle;
    }

    public void setRTtitle(String RTtitle) {
        this.RTtitle = RTtitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Creator<RT> getCREATOR() {
        return CREATOR;
    }

    public RT(boolean trailer, String RTtitle, String content) {
        this.trailer = trailer;
        this.RTtitle = RTtitle;
        this.content = content;
    }

    protected RT(Parcel in) {
        trailer = in.readByte() != 0;
        RTtitle = in.readString();
        content = in.readString();
    }

    public static final Creator<RT> CREATOR = new Creator<RT>() {
        @Override
        public RT createFromParcel(Parcel in) {
            return new RT(in);
        }

        @Override
        public RT[] newArray(int size) {
            return new RT[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (trailer ? 1 : 0));
        dest.writeString(RTtitle);
        dest.writeString(content);
    }
}
