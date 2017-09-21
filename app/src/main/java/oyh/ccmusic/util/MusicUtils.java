package oyh.ccmusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    public static int index = 0;          //歌词检索值
//    public  List<LrcContent> mLrcList;//存放歌词列表对象

    private static LrcProcess mLrcProcess; //歌词处理
    /**
     * 初始化歌曲列表
     */
    public static void initMusicList(Context context) {
        // 获取歌曲列表
        sMusicList.clear();
        sMusicList.addAll(LocalMusicUtils.getInstance(context).queryMusic(Environment.getExternalStorageDirectory().getAbsolutePath()));
    }
    /**
     * 初始化我喜欢列表
     */
    public static void initMusicSQLList(Context context){
//        sMusicSQlList.clear();
        sMusicSQlList.addAll(LocalMusicUtils.getInstance(context).queryMusicSQL(sMusicSQlList));
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

}
