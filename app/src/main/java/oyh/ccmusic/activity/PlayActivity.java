package oyh.ccmusic.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import oyh.ccmusic.R;
import oyh.ccmusic.adapter.LrcView;
import oyh.ccmusic.domain.LrcContent;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.service.LocalMusicService;

@TargetApi(Build.VERSION_CODES.N)
public class PlayActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton bt_play, bt_pre, bt_next;
    private SeekBar seekBar;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private TextView currentTimeTxt, totalTimeTxt;
    private LocalMusicService.CallBack callBack;
    private TextView mMusicTitle,mMusicArtist;
    private int currentPosition;
    private MyHandler mHandler = new MyHandler(this);
    private ImageView coverImage;
    private boolean mFlag = true;
    private ArrayList<Music> musicBeanList = new ArrayList<>();
    private int mProgress;
    private ImageButton mShuffleSong;
    private ImageButton mCurtListSong;
    private ImageButton mRepeatSong;
    public  LrcView lrcView; // 自定义歌词视图
    private String url; // 歌曲路径
    private static int ORDERMODE=0;
    private static int SHUFFLEMODE=1;
    private static int REPEATMODE=2;
    //    默认播放模式为顺序播放
    private static int CURRENTMODE=ORDERMODE;
    private IntentFilter intentFilter;
    private TextView testLrc;
    private  class MyHandler extends Handler {

        private WeakReference<PlayActivity> reference;

        public MyHandler(PlayActivity activity) {
            reference = new WeakReference<>(activity);
        }



        @Override
        public void handleMessage(Message msg) {
            PlayActivity activity = reference.get();
            if (activity != null) {


                int currentTime = activity.callBack.callCurrentTime();
                int totalTime = activity.callBack.callTotalDate();
                activity.seekBar.setMax(totalTime);
                activity.seekBar.setProgress(currentTime);

                String current = format .format(new Date(currentTime));
                String total = format.format(new Date(totalTime));

                activity.currentTimeTxt.setText(current);
                activity.totalTimeTxt.setText(total);

                activity.mMusicTitle.setText(activity.callBack.getTitle());
                activity.mMusicArtist.setText(activity.callBack.getArtist());
            }

        }

    }
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            callBack = (LocalMusicService.MyBinder)service;
            Log.e("LocalMusicService","coon");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            callBack = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_activity_layout);
        initData();
        getMusInfoAndStService();
//        playerMusicByIBinder();
    }

    private void initData(){
        seekBar = findViewById(R.id.seek_bar);
        bt_play = findViewById(R.id.bt_play);
        bt_pre = findViewById(R.id.bt_pre);
        bt_next = findViewById(R.id.bt_next);
        mCurtListSong= findViewById(R.id.im_curplaylist);
        mShuffleSong=  findViewById(R.id.im_shuffleSong);
        mRepeatSong= findViewById(R.id.im_repeatSong);

        lrcView = (LrcView) findViewById(R.id.lrcShowView);
        mMusicTitle=  findViewById(R.id.musicTitle);
        mMusicArtist= findViewById(R.id.musicArtist);


        currentTimeTxt = findViewById(R.id.current_time_txt);
        totalTimeTxt = findViewById(R.id.total_time_txt);



        bt_play.setOnClickListener(this);
        bt_pre.setOnClickListener(this);
        bt_next.setOnClickListener(this);

        mCurtListSong.setOnClickListener(this);
        mShuffleSong.setOnClickListener(this);
        mRepeatSong.setOnClickListener(this);
    }

    private void getMusInfoAndStService(){
        /** 接收音乐列表资源 */
        currentPosition = getIntent().getIntExtra("CURRENT_POSITION", 0);
        //TODO
        /** 构造启动音乐播放服务的Intent，设置音乐资源 */
        Intent intent = new Intent(this, LocalMusicService.class);
        intent.putExtra("CURRENT_POSITION", currentPosition);
//        startService(intent);
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
        //专辑封面
//        coverImage = (ImageView) findViewById(R.id.coverImage);

//        LocalMusicService musicService = new LocalMusicService();
//        musicService.animator = ObjectAnimator.ofFloat(coverImage, "rotation", 0, 359);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * 播放音乐通过Binder接口实现
     */
    public void playerMusicByIBinder() {
        boolean playerState = callBack.isPlayerMusic();
        if (playerState) {
            bt_play.setImageResource(R.drawable.player_btn_pause_normal);
        } else {
            bt_play.setImageResource(R.drawable.player_btn_play_normal);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conn != null || callBack != null) {
//            unbindService(conn);
            callBack = null;
        }
//        Intent intent = new Intent(this, LocalMusicService.class);
//        stopService(intent);
        mFlag = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 播放或者暂停
            case R.id.bt_play:
                Toast.makeText(this,"aaaaaaaa",Toast.LENGTH_LONG).show();
//                callBack.isPlayerMusic();
//                Log.e("PlayActivity","onclick");
//                playerMusicByIBinder();
                break;
            case R.id.bt_pre:
                if (CURRENTMODE == SHUFFLEMODE) {
//                    callBack.shPlayPre();
                } else {
//                    callBack.isPlayPre();
                }

                break;
            case R.id.bt_next:
                if (CURRENTMODE == SHUFFLEMODE) {
//                    callBack.shPlayNext();
                } else {
//                    callBack.isPlayNext();
                }
                break;
        }
    }
}
