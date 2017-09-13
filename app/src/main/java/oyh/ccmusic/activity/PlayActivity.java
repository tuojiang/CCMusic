package oyh.ccmusic.activity;

import android.annotation.TargetApi;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import oyh.ccmusic.R;
import oyh.ccmusic.adapter.LrcView;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

@TargetApi(Build.VERSION_CODES.N)
public class PlayActivity extends MainActivity implements View.OnClickListener {

    private ImageButton bt_play, bt_pre, bt_next;
    private SeekBar seekBar;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private TextView currentTimeTxt, totalTimeTxt;
    private TextView mMusicTitle,mMusicArtist;
    private int currentPosition;
    private int currentTotalTime;
    private MyHandler mHandler = new MyHandler();
    private ImageView coverImage;
    private boolean mFlag = true;
    private ArrayList<Music> musicBeanList = new ArrayList<>();
    private int mProgress;
    private ImageButton mShuffleSong;
    private ImageButton mCurtListSong;
    private ImageButton mRepeatSong;
    private ImageView mBack;
    private boolean isSeekBarChanging;
    private MainActivity mActivity;
    public  LrcView lrcView; // 自定义歌词视图
    private String url; // 歌曲路径
    private MainActivity mainActivity;
    private static int ORDERMODE=0;
    private static int SHUFFLEMODE=1;
    private static int REPEATMODE=2;
    //    默认播放模式为顺序播放
    private static int CURRENTMODE=ORDERMODE;
    private IntentFilter intentFilter;
    private TextView testLrc;
    private  class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

                int currentTime = mainActivity.getLocalMusicService().callCurrentTime();
                int totalTime = mainActivity.getLocalMusicService().callTotalDate();
                seekBar.setMax(totalTime);
                seekBar.setProgress(currentTime);

                String current = format .format(new Date(currentTime));
                String total = format.format(new Date(totalTime));

                currentTimeTxt.setText(current);
                totalTimeTxt.setText(total);

                mMusicTitle.setText(mainActivity.getLocalMusicService().getTitle());
                mMusicArtist.setText(mainActivity.getLocalMusicService().getArtist());


            }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_activity_layout);
        //初始化界面
        initData();
        getMusInfoAndStService();
    }

    private void initData(){
        seekBar = findViewById(R.id.seek_bar);
        bt_play = findViewById(R.id.bt_play);
        bt_pre = findViewById(R.id.bt_pre);
        bt_next = findViewById(R.id.bt_next);
        mBack=findViewById(R.id.iv_play_back);
        mCurtListSong= findViewById(R.id.im_curplaylist);
        mShuffleSong=  findViewById(R.id.im_shuffleSong);
        mRepeatSong= findViewById(R.id.im_repeatSong);

        lrcView =  findViewById(R.id.lrcShowView);
        mMusicTitle=  findViewById(R.id.musicTitle);
        mMusicArtist= findViewById(R.id.musicArtist);

        currentTimeTxt = findViewById(R.id.current_time_txt);
        totalTimeTxt = findViewById(R.id.total_time_txt);


        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        bt_play.setOnClickListener(this);
        bt_pre.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        mBack.setOnClickListener(this);

        mCurtListSong.setOnClickListener(this);
        mShuffleSong.setOnClickListener(this);
        mRepeatSong.setOnClickListener(this);
    }

    private void getMusInfoAndStService(){
        /** 接收音乐列表资源 */
        currentPosition = getIntent().getIntExtra("CURRENT_POSITION", 0);
//        currentTotalTime = getIntent().getIntExtra("TOTALTIME", 0);
        Music music= MusicUtils.sMusicList.get(currentPosition);
        mMusicTitle.setText(music.getTitle());
        mMusicArtist.setText(music.getArtist());
        int totalTime=music.getLength();
        seekBar.setMax(music.getLength());
        String total = format.format(new Date(totalTime));
        totalTimeTxt.setText(total);
//        currentTimeTxt.setText(currentTotalTime);
        //TODO

    }
    /**
     * 进度条处理
     */
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            if (mActivity.getLocalMusicService() != null) {
                mProgress = seekBar.getProgress();
                mActivity.getLocalMusicService().isSeekto(mProgress);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        allowBindService();
    }

    @Override
    protected void onPause() {
        allowUnbindService();
        super.onPause();

    }

    /**
     * 播放音乐通过Binder接口实现
     */
    public void playerMusicByIBinder() {
        boolean playerState = getLocalMusicService().isPlayerMusic();
        if (playerState) {
            bt_play.setImageResource(R.drawable.player_btn_pause_normal);
        } else {
            bt_play.setImageResource(R.drawable.player_btn_play_normal);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                playerMusicByIBinder();
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
            case R.id.iv_play_back:
                finish();
                break;
        }
    }
}
