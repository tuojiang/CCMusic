package oyh.ccmusic.util;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.domain.Music;

/**
 * Created by yihong.ou on 17-9-7.
 */
public class LocalMusicUtils {
    /**
     * 获取目录下的歌曲
     * @param dirName
     */
    public static ArrayList<Music> queryMusic(String dirName) {
        ArrayList<Music> results = new ArrayList<Music>();
        Cursor cursor = MainActivity.context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.DATA + " like ?",
                new String[]{dirName + "%"},
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
    private static boolean isRepeat(String title, String artist) {
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
    private static String getAlbumImage(int albumId) {
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
}