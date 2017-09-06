package oyh.ccmusic.util;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

import oyh.ccmusic.domain.Music;

/**
 * Created by yihong.ou on 17-9-7.
 */
public class MusicUtils {
    // 存放歌曲列表
    public static ArrayList<Music> sMusicList = new ArrayList<Music>();

    public static void initMusicList() {
        // 获取歌曲列表
        sMusicList.clear();
        sMusicList.addAll(LocalMusicUtils.queryMusic(Environment.getExternalStorageDirectory() + File.separator));
    }
}
