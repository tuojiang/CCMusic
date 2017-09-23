package oyh.ccmusic.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.adapter.LrcProcess;
import oyh.ccmusic.adapter.LrcView;
import oyh.ccmusic.domain.LrcContent;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@TargetApi(Build.VERSION_CODES.N)
public class MusicMLDetailFragment extends Fragment implements View.OnClickListener{
    private ImageButton bt_play, bt_pre, bt_next;
    private SeekBar seekBar;
    private MyHandler mHandler = new MyHandler();
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private TextView currentTimeTxt, totalTimeTxt;
    private TextView mMusicTitle,mMusicArtist;
    private int currentPosition;
    private String currentProgress;
    private int myloveposition;
    private boolean playkey;
    private Music music;
    private ImageView coverImage;
    private boolean mFlag = true;
    private ArrayList<Music> musicBeanList = new ArrayList<>();
    private int mProgress;
    private ImageButton mShuffleSong;
    private ImageButton mCurtListSong;
    private ImageButton mRepeatSong;
    private ImageView mBack;
    private Timer timer;
    private int index=0;
    private boolean isSeekBarChanging;
    private MainActivity mActivity;
    public LrcView lrcView; // 自定义歌词视图
    private String url; // 歌曲路径
    private MainActivity mainActivity;
    private LrcProcess mLrcProcess; //歌词处理
    private static int ORDERMODE=0;
    private static int SHUFFLEMODE=1;
    private static int REPEATMODE=2;
    //    默认播放模式为顺序播放
    private static int CURRENTMODE=ORDERMODE;
    public ArrayList<LrcContent> mLrcList;//存放歌词列表对象
    private IntentFilter intentFilter;

    private  class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            currentPosition= (int) MusicUtils.get(mActivity, "mlposition", 0);
            Log.e("MusicMLDetailFragment","mRunnable="+currentPosition);
            /** 接收音乐列表资源 */
            music = MusicUtils.sMusicSQlList.get(currentPosition);
            mMusicTitle.setText(music.getTitle());
            mMusicArtist.setText(music.getArtist());
            int totalTime=music.getLength();
            seekBar.setMax(music.getLength());
            String total = format.format(new Date(totalTime));
            totalTimeTxt.setText(total);
            currentTimeTxt.setText(currentProgress);

            seekBar.setProgress(mActivity.getLocalMusicService().callCurrentTime());
        }

    }

    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            lrcView.setIndex(mActivity.getLocalMusicService().lrcIndex());
            lrcView.invalidate();
            mHandler.postDelayed(mRunnable, 100);
        }
    };

    public MusicMLDetailFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout= inflater.inflate(R.layout.fragment_musicml_detail, container, false);
        initData(layout);
        getMusicLrc();
        seekTime();
        return layout;
    }
    private void seekTime(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mFlag) {
                    if (mActivity.getLocalMusicService() != null) {
                        mHandler.sendMessage(Message.obtain());

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }
    /**
     * 初始化数据
     * @param view
     */
    private void initData(View view){
        seekBar = view.findViewById(R.id.seek_bar_ml);
        bt_play = view.findViewById(R.id.bt_ml_play);
        bt_pre = view.findViewById(R.id.bt_ml_pre);
        bt_next = view.findViewById(R.id.bt_ml_next);
        mBack=view.findViewById(R.id.iv_play_back_ml);
        mCurtListSong= view.findViewById(R.id.im_ml_curplaylist);
        mShuffleSong=  view.findViewById(R.id.im_ml_shuffleSong);
        mRepeatSong= view.findViewById(R.id.im_ml_repeatSong);

        lrcView =  view.findViewById(R.id.lrcShowView_ml);
        mMusicTitle=  view.findViewById(R.id.musicTitle_ml);
        mMusicArtist= view.findViewById(R.id.musicArtist_ml);

        currentTimeTxt = view.findViewById(R.id.current_time_txt_ml);
        totalTimeTxt = view.findViewById(R.id.total_time_txt_ml);


        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        bt_play.setOnClickListener(this);
        bt_pre.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        mBack.setOnClickListener(this);

        mCurtListSong.setOnClickListener(this);
        mShuffleSong.setOnClickListener(this);
        mRepeatSong.setOnClickListener(this);

        if (mActivity.getLocalMusicService().isPlayering()) {
            bt_play.setImageResource(R.drawable.player_btn_pause_normal);
        } else {
            bt_play.setImageResource(R.drawable.player_btn_play_normal);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 播放或者暂停
            case R.id.bt_ml_play:
                playerMusicByIBinder();
                break;
            case R.id.bt_ml_pre:
                if (CURRENTMODE == SHUFFLEMODE) {
                } else {
                }

                break;
            case R.id.bt_ml_next:
                if (CURRENTMODE == SHUFFLEMODE) {
                } else {
                }
                break;
            case R.id.iv_play_back_ml:
                getActivity().onBackPressed();
                break;
        }
    }

    /**
     * 进行歌词处理
     */
    private void getMusicLrc(){
        /**歌词处理*/
        currentPosition= (int) MusicUtils.get(mActivity, "mlposition", 0);
        Log.e("currentPosition","currentPosition="+currentPosition);
//        mActivity.getLocalMusicService().
        //TODO 歌词显示改为专辑旋转
        lrcView.setmLrcList(mLrcList);
        lrcView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.alpha_z));
        mHandler.post(mRunnable);

    }

    /**
     * 播放音乐通过Binder接口实现
     */
    public void playerMusicByIBinder() {
        boolean playerState = mActivity.getLocalMusicService().isPlayerMusic();
        if (playerState) {
            bt_play.setImageResource(R.drawable.player_btn_pause_normal);
        } else {
            bt_play.setImageResource(R.drawable.player_btn_play_normal);
        }
    }
    /**
     * 进度条处理
     */
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            mProgress = progress;
                //更新播放时间0
                currentProgress = format.format(new Date(mProgress));
                currentTimeTxt.setText(currentProgress);

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
}
