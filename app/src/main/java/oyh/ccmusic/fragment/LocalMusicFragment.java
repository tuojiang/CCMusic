package oyh.ccmusic.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.activity.PlayActivity;
import oyh.ccmusic.adapter.LocalMusicListAdapter;
import oyh.ccmusic.adapter.LrcProcess;
import oyh.ccmusic.adapter.LrcView;
import oyh.ccmusic.domain.LrcContent;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

/**
 * 本地列表fragment
 */
@TargetApi(Build.VERSION_CODES.N)
public class LocalMusicFragment extends Fragment implements View.OnClickListener{
    private LrcProcess mLrcProcess; //歌词处理
    private ArrayList<LrcContent> lrcList = new ArrayList<>(); //存放歌词列表对象
    private int currentPos=0;         // 记录当前正在播放的音乐
    private int currentPlayTime=0;
    public LrcView lrcView; // 自定义歌词视图
    private MainActivity mActivity;
    private int mProgress;      //进度条
    private ListView mListView;
    private ImageView mIcon;
    private static SeekBar seekBar;
    private TextView mTitleTextView;
    private TextView mArtTextView;
    private static TextView currentTimeTxt;
    private static TextView totalTimeTxt;
    private Timer timer;
    private boolean isSeekBarChanging;
    private ImageView mIcoImageView;
    private Button nextBtn;
    private ImageButton playBtn;
    private Button preBtn;
    private static SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private ArrayList<Music> mMediaLists = new ArrayList<>();
    private LocalMusicListAdapter adapter= new LocalMusicListAdapter();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public LocalMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    /**
     * 在这里回调通知绑定服务
     */
    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_local_music_list, null);
        setupViews(layout);
//        forSeekBar();
        return layout;
    }

    /**
     * 初始化界面
     * @param layout
     */
    private void setupViews(View layout) {
        mListView=layout.findViewById(R.id.music_list_view);
        seekBar = layout.findViewById(R.id.seek_music);
        mTitleTextView=layout.findViewById(R.id.tv_music_list_title);
        mArtTextView=layout.findViewById(R.id.tv_music_list_artist);
        nextBtn=layout.findViewById(R.id.next_btn);
        playBtn=layout.findViewById(R.id.play_btn);
        preBtn=layout.findViewById(R.id.pre_btn);
        mIcon=layout.findViewById(R.id.music_list_icon);
        currentTimeTxt = layout.findViewById(R.id.played_time);
        totalTimeTxt = layout.findViewById(R.id.duration_time);
        mListView.setOnItemClickListener(mMusicItemClickListener);
        mListView.setAdapter(adapter);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        preBtn.setOnClickListener(this);
        mIcon.setOnClickListener(this);
    }
    /**
     * 进度条处理
     */
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            mProgress = progress;
            //更新播放时间
            String current = format.format(new Date(mProgress));
            currentTimeTxt.setText(current);
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

    /**
     * 监听歌曲点击事件
     */
    private AdapterView.OnItemClickListener mMusicItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
                play(position);
        }
    };

    /**
     * 播放点击歌曲并保存当前位置值
     * @param position
     */
    private void play(int position) {
        int pos=mActivity.getLocalMusicService().play(position);
        currentPos=position;
        updatePanel(pos);

    }

    /**
     * 更新播放歌曲面板
     * @param position
     */
    private void updatePanel(int position) {
        if (MusicUtils.sMusicList.isEmpty()||position<0) return;
        int totalTime=mActivity.getLocalMusicService().callTotalDate();
        int currentTime = mActivity.getLocalMusicService().callCurrentTime();
        currentPlayTime=currentTime;
        seekBar.setMax(totalTime);
        seekBar.setProgress(currentTime);
        String current = format .format(new Date(currentTime));
        String total = format.format(new Date(totalTime));
        Bitmap icon = BitmapFactory.decodeFile(MusicUtils.sMusicList.get(position).getImage());
        mIcon.setImageBitmap(icon);
        currentTimeTxt.setText(current);
        totalTimeTxt.setText(total);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isSeekBarChanging){
                    //TODO 进度条走动时间更新
//                    int currentTime = mActivity.getLocalMusicService().callCurrentTime();
//                    String current = format .format(new Date(currentTime));
//                    currentTimeTxt.setText(current);
                    seekBar.setProgress(mActivity.getLocalMusicService().callCurrentTime());
                }
            }
        },0,100);
        if (mActivity.getLocalMusicService().isPlayering()) {
            playBtn.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            playBtn.setImageResource(R.drawable.list_action_play);
        }
    }

    public void onMusicListChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play_btn:
                if (mActivity.getLocalMusicService().isPlayering()) {
                    mActivity.getLocalMusicService().pause();
                    playBtn.setImageResource(R.drawable.list_action_play);
                } else {
                    play(currentPos); // 播放
                }
                break;
            case R.id.pre_btn:
                mActivity.getLocalMusicService().isPlayPre();
                break;
            case R.id.next_btn:
                mActivity.getLocalMusicService().isPlayNext();
                break;

            case R.id.music_list_icon:
            Intent intent = new Intent(mActivity,PlayActivity.class);
            intent.putExtra("CURRENT_POSITION", currentPos);
//            intent.putExtra("TOTALTIME", currentPlayTime);
            startActivity(intent);
                break;
        }
    }
}
