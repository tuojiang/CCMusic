package oyh.ccmusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import oyh.ccmusic.R;
import oyh.ccmusic.service.LocalMusicService;
import oyh.ccmusic.util.MusicUtils;

/**
 * 启动闪屏页面
 * Created by yihong.ou on 17-9-8.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome_splash_layout);
        //启动后台播放服务
        MusicUtils.initMusicList(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        },2000);

    }
}
