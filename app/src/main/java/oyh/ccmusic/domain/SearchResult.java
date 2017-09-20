package oyh.ccmusic.domain;

import java.io.Serializable;

/**
 * Created by yihong.ou on 17-9-18.
 */
public class SearchResult implements Serializable{
    private static final long serialVersionUID = 0X00000001l;
    private String musicName;
    private String url;
    private String artist;
    private String album;

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
