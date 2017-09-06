package oyh.ccmusic.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yihong.ou on 17-8-28.
 */
public class LrcContent implements Parcelable {
    private String lrcStr;	//歌词内容
    private int lrcTime;	//歌词当前时间
    public String getLrcStr() {
        return lrcStr;
    }
    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }
    public int getLrcTime() {
        return lrcTime;
    }
    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }

    public LrcContent() {
    }

    @Override
    public String toString() {
        return "LrcContent{" +
                "lrcTime=" + lrcTime +
                ", lrcStr='" + lrcStr + '\'' +
                '}';
    }

    public LrcContent(String lrcStr, int lrcTime) {
        this.lrcStr = lrcStr;
        this.lrcTime = lrcTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * 把值写入Parcel中
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(lrcStr);
        parcel.writeInt(lrcTime);
    }
    /**
     * 必须用 public static final 修饰符
     * 对象必须用 CREATOR
     */
    public static final Creator<LrcContent> CREATOR = new Creator<LrcContent>() {
        /**
         * 从Parcel中读取数据
         */
        @Override
        public LrcContent createFromParcel(Parcel source) {
            LrcContent lrcContent=new LrcContent();
            lrcContent.setLrcStr(source.readString());
            lrcContent.setLrcTime(source.readInt());
            return lrcContent;
        }

        @Override
        public LrcContent[] newArray(int size) {
            return new LrcContent[size];
        }

    };
}
