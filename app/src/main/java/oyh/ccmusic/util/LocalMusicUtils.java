package oyh.ccmusic.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

import oyh.ccmusic.Provider.DBHelper;
import oyh.ccmusic.Provider.PlayListContentProvider;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.service.LocalMusicService;

/**
 * 本地音乐列表工具类
 * Created by yihong.ou on 17-9-7.
 */
public class LocalMusicUtils {
    /**
     * 获取目录下的歌曲
     * @param dirName
     */
    private Context mContext;

    private static LocalMusicUtils mInstance;
    private LocalMusicUtils(Context context) {
        mContext = context;
    }

    public static LocalMusicUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LocalMusicUtils.class) {
                if (mInstance == null) {
                    mInstance = new LocalMusicUtils(context);
                }
            }
        }
        return  mInstance;
    }

    public ArrayList<Music> queryMusic(String path) {
        ArrayList<Music> results = new ArrayList<Music>();
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.DATA + " like ?",
                new String[]{path + "%"},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor == null) return results;
        Music music;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String isMusic = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic != null && isMusic.equals("")) continue;

            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            if (isRepeat(title, artist)) continue;

            music = new Music();
            music.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
            music.setTitle(title);
            music.setArtist(artist);
            music.setMusicPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
            music.setLength(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            music.setImage(getAlbumImage(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));
            music.setUrl(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            results.add(music);
        }

        cursor.close();
        return results;
    }
    /**
     * 根据音乐名称和艺术家来判断是否重复包含了
     * @param title
     * @param artist
     * @return
     */
    private boolean isRepeat(String title, String artist) {
        for (Music music : MusicUtils.sMusicList) {
            if (title.equals(music.getTitle()) && artist.equals(music.getArtist())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 根据歌曲id获取图片
     * @param albumId
     * @return
     */
    private String getAlbumImage(int albumId) {
        String result = "";
        Cursor cursor = null;
        try {
            cursor = AppliContext.sContext.getContentResolver().query(
                    Uri.parse("content://media/external/audio/albums/"
                            + albumId), new String[] { "album_art" }, null,
                    null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast();) {
                result = cursor.getString(0);
                break;
            }
        } catch (Exception e) {
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return null == result ? null : result;
    }

    /**
     * 获取流派信息
     * @param audioId
     * @return
     */
    public String getGenres(int audioId){
        String result="";
        Cursor cursor=null;
        try{//TODO 流派信息获取
            cursor=AppliContext.sContext.getContentResolver().query(Uri.parse("content://media/external/audio/media/" + audioId+ "/genres"),
                    new String[]{android.provider.MediaStore.Audio.GenresColumns.NAME}, null, null, null);
            for (cursor.moveToFirst();cursor.isAfterLast();){
                result=cursor.getString(0);
                break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (null!=cursor){
                cursor.close();
            }
        }
        return null == result ? null:result;
    }
    /**
     * 查询本地数据库
     * @return
     * @param sMusicSQlList
     */
    public Collection<? extends Music> queryMusicSQL(ArrayList<Music> sMusicSQlList) {
        sMusicSQlList = new ArrayList<Music>();
        Cursor cursor = mContext.getContentResolver().query(PlayListContentProvider.CONTENT_SONGS_URI,null,null,null,null);

        if (cursor == null) return sMusicSQlList;
        while(cursor.moveToNext()) {
            String musicPath = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.SONG_URI));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ALBUM_URI));
            String title = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ARTIST));
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.DURATION));
            Music mMusic = new Music((int) duration,title,musicPath,image,artist);
            sMusicSQlList.add(mMusic);
        }

        cursor.close();
        return sMusicSQlList;
    }
}
