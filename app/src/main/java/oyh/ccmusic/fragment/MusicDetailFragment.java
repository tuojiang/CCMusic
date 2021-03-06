package oyh.ccmusic.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
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
public class MusicDetailFragment extends Fragment implements View.OnClickListener{
    private ImageButton bt_play, bt_pre, bt_next;
    private SeekBar seekBar;
    private MyHandler mHandler = new MyHandler();
    private java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("mm:ss");
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
    private ImageView mShuffleSong;
    private ImageView mCurtListSong;
    private ImageView mRepeatSong;
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
    int isRepeat=0;
    //    默认播放模式为顺序播放
    private static int CURRENTMODE=ORDERMODE;
    public ArrayList<LrcContent> mLrcList;//存放歌词列表对象

    private  class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            currentPosition= (int) MusicUtils.get(mActivity, "position", 0);
            /** 接收音乐列表资源 */
            music = MusicUtils.sMusicList.get(currentPosition);
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

    public MusicDetailFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout= inflater.inflate(R.layout.fragment_music_detail, container, false);
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
        seekBar = view.findViewById(R.id.seek_bar);
        bt_play = view.findViewById(R.id.bt_play);
        bt_pre = view.findViewById(R.id.bt_pre);
        bt_next = view.findViewById(R.id.bt_next);
        mBack=view.findViewById(R.id.iv_play_back);
        mCurtListSong= view.findViewById(R.id.im_curplaylist);
        mShuffleSong=  view.findViewById(R.id.im_shuffleSong);
        mRepeatSong= view.findViewById(R.id.im_repeatSong);

        lrcView =  view.findViewById(R.id.lrcShowView);
        mMusicTitle=  view.findViewById(R.id.musicTitle);
        mMusicArtist= view.findViewById(R.id.musicArtist);

        currentTimeTxt = view.findViewById(R.id.current_time_txt);
        totalTimeTxt = view.findViewById(R.id.total_time_txt);

        mCurtListSong.setVisibility(View.VISIBLE);
        mShuffleSong.setVisibility(View.INVISIBLE);
        mRepeatSong.setVisibility(View.INVISIBLE);

        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        bt_play.setOnClickListener(this);
        bt_pre.setOnClickListener(this);
        bt_next.setOnClickListener(this);

        mCurtListSong.setOnClickListener(this);
        mShuffleSong.setOnClickListener(this);
        mRepeatSong.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 播放或者暂停
            case R.id.bt_play:
                playerMusicByIBinder();
                break;
            case R.id.bt_pre:
                if (CURRENTMODE == SHUFFLEMODE) {
                    int min=1;
                    int max=MusicUtils.sMusicList.size();
                    Random random = new Random();
                    final int s = random.nextInt(max-min+1) + min;
                    mActivity.getLocalMusicService().play(s);
                    mLrcList = mActivity.getLocalMusicService().initLrcx(mLrcList,s);
                    lrcView.setmLrcList(mLrcList);
                    lrcView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.alpha_z));
                    mHandler.post(mRunnable);
                } else {
                    mActivity.getLocalMusicService().isPlayPre();
                    mLrcList = mActivity.getLocalMusicService().initLrcx(mLrcList,currentPosition-1);
                    lrcView.setmLrcList(mLrcList);
                    lrcView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.alpha_z));
                    mHandler.post(mRunnable);
                }

                break;
            case R.id.bt_next:
                if (CURRENTMODE == SHUFFLEMODE) {
                    int min=0;
                    int max=MusicUtils.sMusicList.size();
                    Random random = new Random();
                    final int s = random.nextInt(max-min+1) + min;
                    mActivity.getLocalMusicService().play(s);
                    mLrcList = mActivity.getLocalMusicService().initLrcx(mLrcList,s);
                    lrcView.setmLrcList(mLrcList);
                    lrcView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.alpha_z));
                    mHandler.post(mRunnable);
                }else {
                    mActivity.getLocalMusicService().isPlayNext();
                    mLrcList = mActivity.getLocalMusicService().initLrcx(mLrcList,currentPosition+1);
                    lrcView.setmLrcList(mLrcList);
                    lrcView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.alpha_z));
                    mHandler.post(mRunnable);
                }

                break;
            case R.id.im_curplaylist:
                mShuffleSong.setVisibility(View.VISIBLE);
                mCurtListSong.setVisibility(View.INVISIBLE);
                mRepeatSong.setVisibility(View.INVISIBLE);
                CURRENTMODE=SHUFFLEMODE;
                Toast.makeText(mActivity,"随机播放",Toast.LENGTH_LONG).show();
                isRepeat=0;
                MusicUtils.put("isRepeat", isRepeat);
                break;
            case R.id.im_shuffleSong:
                mRepeatSong.setVisibility(View.VISIBLE);
                mShuffleSong.setVisibility(View.INVISIBLE);
                mCurtListSong.setVisibility(View.INVISIBLE);
                CURRENTMODE=REPEATMODE;
                Toast.makeText(mActivity,"单曲循环",Toast.LENGTH_LONG).show();
                isRepeat=1;
                MusicUtils.put("isRepeat", isRepeat);

                break;
            case R.id.im_repeatSong:
                mCurtListSong.setVisibility(View.VISIBLE);
                mShuffleSong.setVisibility(View.INVISIBLE);
                mRepeatSong.setVisibility(View.INVISIBLE);
                CURRENTMODE=ORDERMODE;
                Toast.makeText(mActivity,"顺序播放",Toast.LENGTH_LONG).show();
                isRepeat=0;
                MusicUtils.put("isRepeat", isRepeat);
                break;
        }
    }

    /**
     * 进行歌词处理
     */
    private void getMusicLrc(){
        /**歌词处理*/
        currentPosition= (int) MusicUtils.get(mActivity, "position", 0);
        mLrcList = mActivity.getLocalMusicService().initLrcx(mLrcList,currentPosition);
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

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            mProgress = progress;
                //更新播放时间0
                currentProgress = format.format(new Date(mProgress));
                currentTimeTxt.setText(currentProgress);

        }

        /*滚动时,应当暂停后台定时器*/
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        /*滑动结束后，重新设置值*/
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            if (mActivity.getLocalMusicService() != null) {
                mProgress = seekBar.getProgress();
                mActivity.getLocalMusicService().isSeekto(mProgress);
            }
        }
    }
}
