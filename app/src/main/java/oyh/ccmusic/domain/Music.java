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
    private String albumName;//专辑名称
    private int albumSongs;//专辑下歌曲数
    private int artistSongs;//艺术家下歌曲数
    private int artistAlbums;//艺术家下专辑数
    private String genres;//流派信息
    private int genresSongs;//流派歌曲数

    public Music(int length, String title, String url, String image, String artist) {
        this.length = length;
        this.title = title;
        this.url = url;
        this.image = image;
        this.artist = artist;
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
                ", albumName='" + albumName + '\'' +
                ", albumSongs=" + albumSongs +
                ", artistSongs=" + artistSongs +
                ", artistAlbums=" + artistAlbums +
                ", genres='" + genres + '\'' +
                ", genresSongs=" + genresSongs +
                '}';
    }

    public int getGenresSongs() {
        return genresSongs;
    }

    public void setGenresSongs(int genresSongs) {
        this.genresSongs = genresSongs;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getAlbumSongs() {
        return albumSongs;
    }

    public void setAlbumSongs(int albumSongs) {
        this.albumSongs = albumSongs;
    }

    public int getArtistSongs() {
        return artistSongs;
    }

    public void setArtistSongs(int artistSongs) {
        this.artistSongs = artistSongs;
    }

    public int getArtistAlbums() {
        return artistAlbums;
    }

    public void setArtistAlbums(int artistAlbums) {
        this.artistAlbums = artistAlbums;
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
        dest.writeString(albumName);
        dest.writeInt(albumSongs);
        dest.writeInt(artistSongs);
        dest.writeInt(artistAlbums);
        dest.writeString(genres);
        dest.writeInt(genresSongs);
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
            music.setAlbumName(source.readString());
            music.setAlbumSongs(source.readInt());
            music.setArtistSongs(source.readInt());
            music.setArtistAlbums(source.readInt());
            music.setGenres(source.readString());
            music.setGenresSongs(source.readInt());
            return music;
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }

    };
}
