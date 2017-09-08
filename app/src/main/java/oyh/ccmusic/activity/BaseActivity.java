package oyh.ccmusic.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import oyh.ccmusic.service.LocalMusicService;

/**
 * 基类:复用的代码都放在这边
 */
public class BaseActivity extends FragmentActivity {
    protected LocalMusicService.CallBack callBack;

    private ServiceConnection localplayServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            callBack= (LocalMusicService.CallBack) iBinder;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            callBack=null;

        }
    };
    /**
     * Fragment的view加载完成后回调
     */
    public void allowBindService() {
        bindService(new Intent(this, LocalMusicService.class), localplayServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    /**
     * fragment的view消失后回调
     */
    public void allowUnbindService() {
//        unbindService(localplayServiceConnection);
    }
}
