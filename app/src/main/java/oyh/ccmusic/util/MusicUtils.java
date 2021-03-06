package oyh.ccmusic.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.domain.LrcContent;
import oyh.ccmusic.domain.Music;


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
    // 初始化公共列表
    public static ArrayList<Music> commonList =  new ArrayList<>();
    // 存放item列表
    public static ArrayList<Music> itemCommonList =  new ArrayList<>();
    //本地搜索初始化列表
    public static ArrayList<String> localSearchList =  new ArrayList<>();
    public static ArrayList<Music> myloveList =  new ArrayList<>();
    public static ArrayList<LrcContent> mLrcList =  new ArrayList<>();
    public static HashMap<String, String> map = new HashMap<String, String>();
    /**
     * 初始化歌曲列表
     */
    public static void initMusicList(Context context) {
        // 获取歌曲列表
        sMusicList.clear();
        sMusicList.addAll(LocalMusicUtils.getInstance(context).queryMusic(Environment.getExternalStorageDirectory().getAbsolutePath()));
        commonList.addAll(sMusicList);
    }
    /**
     * 初始化歌词
     */
    public static void initLrcList(Context context,List<LrcContent> list) {
        // 获取歌曲列表
        mLrcList.clear();
        mLrcList.addAll(list);
    }
    /**
     * 根据名称进行查询
     * @param context
     * @param key
     * @return
     */
    public static String[] queryMusicName(Context context, String key) {
        ArrayList<String> nameList = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Audio.Media.DISPLAY_NAME + " LIKE '%" + key + "%'",
                    null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            while (c.moveToNext()) {
                String title = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));//歌曲名
                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                nameList.add(name);
                localSearchList.add(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        if (nameList.isEmpty()){
            return new String[]{};
        }
        return (String[])nameList.toArray(new String[nameList.size()]);
    }
    /**
     * 初始化流派列表
     * @param context
     * @param list
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static void initGenresList(Context context, ArrayList list){
        Set set = new HashSet();
        List newList = new ArrayList();
        for (int i=0;i<list.size();i++) {
            Music music = (Music) list.get(i);
            String genres = music.getGenres();
            if (set.add(genres)) {
                newList.add(music);
            }
            String count = map.get(genres);
            if (count == null) {
                map.put(genres, "1");
            } else {
                map.put(genres, (Integer.parseInt(count) + 1)+"");
            }
        }

        genreslList.addAll(newList);
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
        artistlList.addAll(newList);

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
        albumlList.addAll(newList);

    }
    /**
     * 初始化我喜欢列表
     */
    public static void initMusicSQLList(Context context){
        sMusicSQlList.addAll(LocalMusicUtils.getInstance(context).queryMusicSQL(sMusicSQlList));
        myloveList.addAll(sMusicSQlList);
    }
    /**
     * 清空我喜欢列表
     */
    public static void removeMusicSQLList(Context context){
        sMusicSQlList.clear();
    }

    /**
     * 查询Album item列表的数据个数
     * @param albumName
     * @param list
     */
    public static void queryItem(String albumName,ArrayList<Music> list){
        List newList = new ArrayList();
        for (int i=0;i<list.size();i++){
            Music music= list.get(i);
            if (albumName.equals(music.getAlbumName()))
                newList.add(music);

        }
        itemCommonList.clear();
        itemCommonList.addAll(newList);

    }
    /**
     * 查询Artist item列表的数据个数
     * @param artist
     * @param list
     */
    public static void queryArtistItem(String artist,ArrayList<Music> list){
        List newList = new ArrayList();
        for (int i=0;i<list.size();i++){
            Music music= list.get(i);
            if (artist.equals(music.getArtist()))
                newList.add(music);
        }
        itemCommonList.clear();
        itemCommonList.addAll(newList);

    }
    /**
     * 查询Genres item列表的数据个数
     * @param name
     * @param list
     */
    public static void queryGenrestItem(String name,ArrayList<Music> list){
        List newList = new ArrayList();
        for (int i=0;i<list.size();i++){
            Music music= list.get(i);
            if (name.equals(music.getGenres()))
                newList.add(music);
        }
        itemCommonList.clear();
        itemCommonList.addAll(newList);

    }

    /**
     * 根据myloveList属性查询在sMusicList中位置
     * @param position
     * @return
     */
    public static int queryMLoveToList(int position){
        if(position < 0) position = 0;
        if(position >= myloveList.size()) position = myloveList.size() - 1;
        String name=myloveList.get(position).getTitle();
        int index=0;
        for (int i=0;i<sMusicList.size();i++){
            Music music= sMusicList.get(i);
            if (name.equals(music.getTitle())) {
                index = i;
            }else {
                continue;
            }
        }
        return index;
    }

    /**
     * 根据歌名查询在sMusicList中位置
     * @param name
     * @return
     */
    public static int queryNameToList(String name){
        int index=0;
        for (int i=0;i<sMusicList.size();i++){
            Music music= sMusicList.get(i);
            if (name.equals(music.getTitle())) {
                index = i;
            }else {
                continue;
            }
        }
        return index;
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

    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}
