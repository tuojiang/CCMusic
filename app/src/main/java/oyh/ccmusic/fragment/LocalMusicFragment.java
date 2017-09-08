package oyh.ccmusic.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.adapter.LocalMusicListAdapter;
import oyh.ccmusic.adapter.LrcProcess;
import oyh.ccmusic.adapter.LrcView;
import oyh.ccmusic.domain.LrcContent;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.service.LocalMusicService;
import oyh.ccmusic.util.MusicUtils;

/**
 * 本地列表fragment
 */
public class LocalMusicFragment extends Fragment {
    private LrcProcess mLrcProcess; //歌词处理
    private ArrayList<LrcContent> lrcList = new ArrayList<>(); //存放歌词列表对象
    private int currentPos;         // 记录当前正在播放的音乐
    public LrcView lrcView; // 自定义歌词视图
    private MainActivity mActivity;
    private ListView mListView;
    private SeekBar seekBar;
    private TextView mTitleTextView;
    private TextView mArtTextView;
    private ImageView mIcoImageView;
    private Button nextBtn;
    private Button playBtn;
    private Button preBtn;
    private Handler mHandler = new Handler();
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
//        mActivity.allowBindService();
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivity.allowUnbindService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_local_music_list, null);
        setupViews(layout);
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

        mListView.setOnItemClickListener(mMusicItemClickListener);
        mListView.setAdapter(adapter);

    }

    /**
     * 监听歌曲点击事件
     */
    private AdapterView.OnItemClickListener mMusicItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            playSong(position);
        }
    };

    /**
     * 播放歌曲的刷新面板
     * @param position
     */
    private void playSong(int position) {
        Intent intent = new Intent(mActivity, LocalMusicService.class);
        intent.putExtra("CURRENT_POSITION", position);
        mActivity.allowBindService();
        updatePanel(position);
    }

    /**
     * 更新面板的信息
     * @param position
     */
    private void updatePanel(int position) {
        if (MusicUtils.sMusicList.isEmpty()||position<0) return;
        seekBar.setMax(mActivity.getLocalMusicService().callTotalDate());

    }

    public void onMusicListChanged() {
        adapter.notifyDataSetChanged();
    }
}
