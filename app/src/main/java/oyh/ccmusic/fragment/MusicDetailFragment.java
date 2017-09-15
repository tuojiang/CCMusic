package oyh.ccmusic.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.adapter.LrcView;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@TargetApi(Build.VERSION_CODES.N)
public class MusicDetailFragment extends Fragment implements View.OnClickListener{
    private ImageButton bt_play, bt_pre, bt_next;
    private SeekBar seekBar;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private TextView currentTimeTxt, totalTimeTxt;
    private TextView mMusicTitle,mMusicArtist;
    private int currentPosition;
    private String currentProgress;
//    private MyHandler mHandler = new MyHandler(this);
    private ImageView coverImage;
    private boolean mFlag = true;
    private ArrayList<Music> musicBeanList = new ArrayList<>();
    private int mProgress;
    private ImageButton mShuffleSong;
    private ImageButton mCurtListSong;
    private ImageButton mRepeatSong;
    private ImageView mBack;
    private Timer timer;
    private boolean isSeekBarChanging;
    private MainActivity mActivity;
    public LrcView lrcView; // 自定义歌词视图
    private String url; // 歌曲路径
    private MainActivity mainActivity;
    private static int ORDERMODE=0;
    private static int SHUFFLEMODE=1;
    private static int REPEATMODE=2;
    //    默认播放模式为顺序播放
    private static int CURRENTMODE=ORDERMODE;
    private IntentFilter intentFilter;
    private TextView testLrc;
    public MusicDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout= inflater.inflate(R.layout.fragment_music_detail, container, false);
        initData(layout);
        getMusInfoAndStService();
        return layout;
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


        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        bt_play.setOnClickListener(this);
        bt_pre.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        mBack.setOnClickListener(this);

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
                } else {
                }

                break;
            case R.id.bt_next:
                if (CURRENTMODE == SHUFFLEMODE) {
                } else {
                }
                break;
            case R.id.iv_play_back:
                getActivity().onBackPressed();
                break;
        }
    }

    /**
     * 进行数据处理
     */
    private void getMusInfoAndStService(){
        /** 接收音乐列表资源 */
        Music music= MusicUtils.sMusicList.get(currentPosition);
        mMusicTitle.setText(music.getTitle());
        mMusicArtist.setText(music.getArtist());
        int totalTime=music.getLength();
        seekBar.setMax(music.getLength());
        String total = format.format(new Date(totalTime));
        totalTimeTxt.setText(total);
        currentTimeTxt.setText(currentProgress);
        //TODO 歌词同步
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isSeekBarChanging){
                    seekBar.setProgress(mActivity.getLocalMusicService().callCurrentTime());
                }
            }
        },0,100);
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
