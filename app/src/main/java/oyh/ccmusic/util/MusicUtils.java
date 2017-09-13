package oyh.ccmusic.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.domain.Music;

/**
 * 歌曲列表工具类
 * Created by yihong.ou on 17-9-7.
 */
public class MusicUtils {
    // 存放歌曲列表
    public static ArrayList<Music> sMusicList = new ArrayList<>();

    /**
     * 初始化歌曲列表
     */
    public static void initMusicList(Context context) {
        // 获取歌曲列表
        sMusicList.clear();
        sMusicList.addAll(LocalMusicUtils.getInstance(context).queryMusic(Environment.getExternalStorageDirectory().getAbsolutePath()));
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
}
