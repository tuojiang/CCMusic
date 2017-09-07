package oyh.ccmusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * 本地音乐服务类
 * Created by yihong.ou on 17-9-8.
 */
public class LocalMusicService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
