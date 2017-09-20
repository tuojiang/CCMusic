package oyh.ccmusic.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yihong.ou on 17-8-21.
 */
public class Music implements Parcelable {
    private String musicName;
    private String musicPath;//歌曲路径
    private String image; // icon
    private String artist; // 艺术家
    private int length; // 长度
    private int id; // 音乐id
    private String title; // 音乐标题
    private String url; // 歌曲路径 5
    private String lrcTitle; // 歌词名称
    private String lrcSize; // 歌词大小

    public Music(String musicName, String musicPath, String image, String artist, int length, int id, String title, String url, String lrcTitle, String lrcSize) {
        this.musicName = musicName;
        this.musicPath = musicPath;
        this.image = image;
        this.artist = artist;
        this.length = length;
        this.id = id;
        this.title = title;
        this.url = url;
        this.lrcTitle = lrcTitle;
        this.lrcSize = lrcSize;
    }

    public Music() {

    }

    @Override
    public String toString() {
        return "Music{" +
                "musicName='" + musicName + '\'' +
                ", musicPath='" + musicPath + '\'' +
                ", image='" + image + '\'' +
                ", artist='" + artist + '\'' +
                ", length=" + length +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", lrcTitle='" + lrcTitle + '\'' +
                ", lrcSize='" + lrcSize + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLrcTitle() {
        return lrcTitle;
    }

    public void setLrcTitle(String lrcTitle) {
        this.lrcTitle = lrcTitle;
    }

    public String getLrcSize() {
        return lrcSize;
    }

    public void setLrcSize(String lrcSize) {
        this.lrcSize = lrcSize;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * 把值写入Parcel中
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(musicName);
        dest.writeString(musicPath);
        dest.writeString(image);
        dest.writeString(artist);
        dest.writeInt(length);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(lrcTitle);
        dest.writeString(lrcSize);
    }

    /**
     * 必须用 public static final 修饰符
     * 对象必须用 CREATOR
     */
    public static final Creator<Music> CREATOR = new Creator<Music>() {
        /**
         * 从Parcel中读取数据
         */
        @Override
        public Music createFromParcel(Parcel source) {

            Music music = new Music();
            music.setMusicName(source.readString());
            music.setMusicPath(source.readString());
            music.setImage(source.readString());
            music.setArtist(source.readString());
            music.setLength(source.readInt());
            music.setId(source.readInt());
            music.setTitle(source.readString());
            music.setUrl(source.readString());
            music.setLrcTitle(source.readString());
            music.setLrcSize(source.readString());
            return music;
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }

    };
}
