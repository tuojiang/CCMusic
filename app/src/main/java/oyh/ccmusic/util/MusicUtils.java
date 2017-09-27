package oyh.ccmusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.adapter.LrcProcess;
import oyh.ccmusic.domain.LrcContent;
import oyh.ccmusic.domain.Music;

import static android.R.attr.duration;

/**
 * 歌曲列表工具类
 * Created by yihong.ou on 17-9-7.
 */
public class MusicUtils {
    // 存放歌曲列表 sd
    public static ArrayList<Music> sMusicList = new ArrayList<>();
    // 存放歌曲列表 数据库
    public static ArrayList<Music> sMusicSQlList = new ArrayList<>();
    // 存放专辑歌阙列表
    public static ArrayList<Music> albumlList =  new ArrayList<>();
    // 存放艺术家列表
    public static ArrayList<Music> artistlList =  new ArrayList<>();
    // 存放流派列表
    public static ArrayList<Music> genreslList =  new ArrayList<>();
    public static ArrayList<Music> commonList =  new ArrayList<>();
    public static ArrayList<Music> commonList1 =  new ArrayList<>();
    public static ArrayList<Music> commonList2 =  new ArrayList<>();
    public static int index = 0;          //歌词检索值
    private static LrcProcess mLrcProcess; //歌词处理
    /**
     * 初始化歌曲列表
     */
    public static void initMusicList(Context context) {
        // 获取歌曲列表
        sMusicList.clear();
        sMusicList.addAll(LocalMusicUtils.getInstance(context).queryMusic(Environment.getExternalStorageDirectory().getAbsolutePath()));
        commonList.addAll(sMusicList);
        commonList1.addAll(sMusicList);
        commonList2.addAll(sMusicList);
    }
    /**
     * 初始化流派列表
     * @param context
     * @param list
     */
    public static void initGenresList(Context context,ArrayList list){
        Set set = new HashSet();
        List newList = new ArrayList();
        for (int i=0;i<list.size();i++){
            Music music= (Music) list.get(i);
            String genres=music.getGenres();
            if (set.add(genres))
                newList.add(music);
        }
        list.clear();
        genreslList.addAll(newList);
        Log.e("initGenresList","siz="+String.valueOf(genreslList.size()));
    }
    /**
     * 初始化艺术家列表
     * @param context
     * @param list
     */
    public static void initArtistList(Context context,ArrayList list){
        Set set = new HashSet();
        List newList = new ArrayList();
        for (int i=0;i<list.size();i++){
            Music music= (Music) list.get(i);
            String artistName=music.getArtist();
            if (set.add(artistName))
                newList.add(music);
        }
        list.clear();
        artistlList.addAll(newList);
        Log.e("initArtistList","siz="+String.valueOf(artistlList.size()));

    }
    /**
     * 初始化专辑列表
     * @param context
     * @param list
     */
    public static void initAlbumList(Context context,ArrayList list){
        Set set = new HashSet();
        List newList = new ArrayList();
        for (int i=0;i<list.size();i++){
            Music music= (Music) list.get(i);
        String albumName=music.getAlbumName();
            if (set.add(albumName))
                newList.add(music);
        }
        list.clear();
        albumlList.addAll(newList);
        Log.e("initAlbumList","siz="+String.valueOf(albumlList.size()));

    }

    /**
     * 初始化我喜欢列表
     */
    public static void initMusicSQLList(Context context){
        sMusicSQlList.addAll(LocalMusicUtils.getInstance(context).queryMusicSQL(sMusicSQlList));
    }
    /**
     * 清空我喜欢列表
     */
    public static void removeMusicSQLList(Context context){
        sMusicSQlList.clear();
    }
    /**
     * 获取sd卡路径
     * @return
     */
    public static String getBaseDir() {
        String dir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            //dir = AppliContext.sContext.getFilesDir() + File.separator;
            dir = null;
        }
        return dir;
    }

    /**
     * 存放播放位置
     * @param key
     * @param value
     */
    public static void put(final String key,final Object value) {
        SharedPreferences sp = AppliContext.sContext.getSharedPreferences("position",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }else if(value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }else if(value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }else if(value instanceof Long) {
            editor.putLong(key, (Long) value);
        }else {
            editor.putString(key, (String) value);
        }

        editor.commit();
    }

    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = AppliContext.sContext.getSharedPreferences("position",
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return defaultObject;
    }
    /**
     * 根据音乐名称和艺术家来判断是否重复包含了
     * @param albumName
     * @param artist
     * @return
     */
    private static boolean isRepeat(String albumName, String artist) {
        for (Music music : MusicUtils.sMusicList) {
            if (albumName.equals(music.getAlbumName()) && artist.equals(music.getArtist())) {
                return true;
            }
        }
        return false;
    }
}
