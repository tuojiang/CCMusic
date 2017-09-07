package oyh.ccmusic.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import oyh.ccmusic.service.LocalMusicService;

/**
 * 基类:复用的代码都放在这边
 */
public class BaseActivity extends FragmentActivity {
    protected LocalMusicService localMusicService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
