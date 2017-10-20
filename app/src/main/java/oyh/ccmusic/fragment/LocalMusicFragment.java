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
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
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
    private int currentAdd=0;
    private int currentPosition=1;
    private boolean mFlag = true;
    public LrcView lrcView; // 自定义歌词视图
    private MainActivity mActivity;
    private int mProgress;      //进度条
    private ListView mListView;
    private ImageView mIcon;
    private static SeekBar seekBar;
    public SeekBar seekBarPlay;
    public Message message;
    public String currentProgress;
    private TextView mTitleTextView;
    private TextView mArtTextView;
    private static TextView currentTimeTxt;
    private static TextView totalTimeTxt;
    private Timer timer;
    private boolean isSeekBarChanging;
    private ImageView mIcoImageView;
    private Music music;
    private Button nextBtn;
    private ImageButton playBtn;
    private Button preBtn;
    private MyHandler mHandler = new MyHandler();
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;
    private static SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private ArrayList<Music> mMediaLists = new ArrayList<>();
    private LocalMusicListAdapter adapter= new LocalMusicListAdapter(mActivity);
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


    @Override
    public void onResume() {
        int index = (int) MusicUtils.get(AppliContext.sContext,"searchps",0);
        currentPos=index;
        seekTime();

        super.onResume();
        Log.e("Resume","resume");
    }

    private  class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            currentPosition= (int) MusicUtils.get(mActivity, "position", 0);
            int totalTime=mActivity.getLocalMusicService().callTotalDate();
            int currentTime = mActivity.getLocalMusicService().callCurrentTime();
            currentPlayTime=currentTime;
            seekBar.setMax(totalTime);
            seekBar.setProgress(currentTime);
            String current = format .format(new Date(currentTime));
            String total = format.format(new Date(totalTime));
            Bitmap icon = BitmapFactory.decodeFile(MusicUtils.sMusicList.get(currentPosition).getImage());
            mIcon.setImageBitmap(icon==null ? BitmapFactory.decodeResource(
                    getResources(), R.mipmap.img) : icon);
            currentTimeTxt.setText(current);
            totalTimeTxt.setText(total);
                        seekBar.setProgress(mActivity.getLocalMusicService().callCurrentTime());
            if (mActivity.getLocalMusicService().isPlayering()) {
                playBtn.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                playBtn.setImageResource(R.drawable.list_action_play);
            }


        }

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
        registerForContextMenu(mListView);
        mListView.setAdapter(adapter);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        preBtn.setOnClickListener(this);
        mIcon.setOnClickListener(this);
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
     * 进度条处理
     */
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            mProgress = progress;
            //更新播放时间0
            currentProgress = format.format(new Date(mProgress));
            currentTimeTxt.setText(currentProgress);
            Log.e("Local",currentProgress);
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
            int a=MusicUtils.sMusicList.size();
        }
    };

    /**
     * 播放点击歌曲并保存当前位置值
     * @param position
     */
    private void play(int position) {
        int pos=mActivity.getLocalMusicService().play(position);
        currentPos=position;
        seekTime();
    }


    public void onMusicListChanged() {
        adapter.notifyDataSetChanged();
    }
    /**
     * 播放音乐通过Callback对象实现
     */
    public void playerMusicByIBinder() {
        boolean playerState = mActivity.getLocalMusicService().isPlayerMusic();
        if (playerState) {
            playBtn.setImageResource(R.drawable.player_btn_pause_normal);
        } else {
            playBtn.setImageResource(R.drawable.player_btn_play_normal);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play_btn:
                playerMusicByIBinder();
                break;
            case R.id.pre_btn:
                mActivity.getLocalMusicService().isPlayPre();
                break;
            case R.id.next_btn:
                mActivity.getLocalMusicService().isPlayNext();
                break;

            case R.id.music_list_icon:
                mActivity.Visiable();
                fragmentManager=getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                MusicDetailFragment musicDetailFragment=new MusicDetailFragment();
                transaction.add(R.id.music_detail_fragment,musicDetailFragment).addToBackStack(null).commit();
                break;
        }
    }

    public class LocalMusicListAdapter extends BaseAdapter {
        private static final String TAG = "LocalMusicListAdapter";
        private View view;
        public LocalMusicListAdapter(MainActivity mActivity) {
        }

        public void setPlayingPosition() {
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return MusicUtils.sMusicList.size();
        }

        @Override
        public Object getItem(int position) {
            return MusicUtils.sMusicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "LocalMusicListAdapter count: " + getCount());
            ViewHolder viewHolder= null;
            MyListener myListener=null;
            if (convertView == null) {
                convertView = View.inflate(AppliContext.sContext, R.layout.local_music_item, null);
                viewHolder = new ViewHolder();
                viewHolder.icon = convertView.findViewById(R.id.music_list_icon);
                viewHolder.title = convertView.findViewById(R.id.tv_music_list_title);
                viewHolder.artist = convertView.findViewById(R.id.tv_music_list_artist);
                viewHolder.menuImage=convertView.findViewById(R.id.iv_aplist_btn);
                viewHolder.mark = convertView.findViewById(R.id.music_list_selected);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            myListener=new MyListener(position);
            Music music = (Music) getItem(position);

            Bitmap icon = BitmapFactory.decodeFile(music.getImage());
            viewHolder.icon.setImageBitmap(icon == null ?
                    BitmapFactory.decodeResource(
                            AppliContext.sContext.getResources(), R.mipmap.img) : icon);
            viewHolder.title.setText(music.getTitle());
            viewHolder.artist.setText(music.getArtist());
            viewHolder.menuImage.setOnClickListener(myListener);

            view=convertView;
            return convertView;
        }

        private class MenuListener implements PopupMenu.OnMenuItemClickListener{
            Music music;
            int positions;
            public MenuListener(int position){
                positions=position;
            }
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                music=MusicUtils.sMusicList.get(positions);
                String title=music.getTitle();
                String path=music.getMusicPath();
                int id = menuItem.getItemId();
                if (id == R.id.add_list_popmenu) {
                    Toast.makeText(AppliContext.sContext,title+"添加到播放列表", Toast.LENGTH_SHORT).show();
                    mActivity.getLocalMusicService().addplaylist(music);
                } else if (id == R.id.del_list_popmenu) {
                    Toast.makeText(AppliContext.sContext, "从播放列表删除"+title, Toast.LENGTH_SHORT).show();
                    mActivity.getLocalMusicService().delplaylist(title);
                }else if (id == R.id.del_sdlist_popmenu){
                    Toast.makeText(AppliContext.sContext, "从SD卡删除"+title, Toast.LENGTH_SHORT).show();
                    mActivity.getLocalMusicService().delplaysdcard(path);
                    adapter.setPlayingPosition();
                }

                return true;
            }
        }

        private class MyListener implements View.OnClickListener {
            int mPosition;
            Music music;
            public MyListener(int inPosition){
                mPosition= inPosition;
            }
            @Override
            public void onClick(View v) {
                music= (Music) getItem(mPosition);
                String title=music.getTitle();
                //将指定的菜单布局进行加载
                PopupMenu popupMenu=new PopupMenu(AppliContext.sContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_pop_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new MenuListener(mPosition));//给菜单绑定监听
                //展示菜单
                popupMenu.show();
            }

        }
        class ViewHolder {
            ImageView icon;
            TextView title, artist;
            ImageView menuImage;
            View mark;
        }
    }
}
