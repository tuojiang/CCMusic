package oyh.ccmusic.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.adapter.LrcProcess;
import oyh.ccmusic.adapter.LrcView;
import oyh.ccmusic.domain.LrcContent;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.BarChartView;
import oyh.ccmusic.util.ImageUtils;
import oyh.ccmusic.util.MusicMemoryCacheUtils;
import oyh.ccmusic.util.MusicUtils;
import oyh.ccmusic.util.SlidingUpPanelLayout;

import static oyh.ccmusic.R.id.sliding_up_content_container;

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
    private int width;
    private int hight;
    private String tagb;
    private int currentPosition=1;
    private boolean mFlag = true;
    public LrcView lrcView; // 自定义歌词视图
    private MainActivity mActivity;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private LinearLayout mlinearLayout;
    private View mfragmwntlinearLayout;
    private android.app.Fragment fragment;
    private int mProgress;      //进度条
    private ListView mListView;
    private GridView mGridView;
    private ImageView mIcon;
    private static SeekBar seekBar;
    public SeekBar seekBarPlay;
    public Message message;
    public String currentProgress;
    private TextView mTitleTextView;
    private TextView mArtTextView;
    private static TextView currentTimeTxt;
    private static TextView totalTimeTxt;
    private boolean isSeekBarChanging;
    private ImageView mIcoImageView;
    private Music music;
    private Button nextBtn;
    private ImageButton playBtn;
    private Button preBtn;
    private BarChartView realView;
    private BarChartView realView2;
    private List viewList=new ArrayList();
    public static Boolean isGridView;
    public View gridView;
    private MyHandler mHandler = new MyHandler();
    private UpdateViewReceiver updateViewReceiver;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;
    private static java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("mm:ss");
    private LocalMusicListAdapter adapter= new LocalMusicListAdapter(mActivity);
    private LocalMusicListAdapter gridadapter;
    private LocalMusicListAdapter listadapter;

    private String UPDATE_VIEW="oyh.ccmusic.updateview";
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
    public void onStart() {
        fragmentManager=getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        MusicDetailFragment musicDetailFragment=new MusicDetailFragment();
        transaction.replace(sliding_up_content_container,musicDetailFragment).addToBackStack(null).commit();
        super.onStart();
    }

    @Override
    public void onResume() {
        int index = (int) MusicUtils.get(AppliContext.sContext,"searchps",0);
                currentPos = index;
                seekTime();
        super.onResume();
    }
    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            lrcView.setIndex(mActivity.getLocalMusicService().lrcIndex());
            lrcView.invalidate();
            mHandler.postDelayed(mRunnable, 100);
        }
    };
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
//            Bitmap icon = BitmapFactory.decodeFile(MusicUtils.sMusicList.get(currentPosition).getImage());
//            mIcon.setImageBitmap(icon==null ? BitmapFactory.decodeResource(
//                    getResources(), R.mipmap.img) : icon);
            Bitmap icon = MusicMemoryCacheUtils.getInstance().load(MusicUtils.sMusicList.get(currentPosition).getImage());
            mIcon.setImageBitmap(icon==null ? ImageUtils
                    .scaleBitmap(R.mipmap.img) : ImageUtils.scaleBitmap(icon));
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

    private class UpdateViewReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            isGridView=intent.getBooleanExtra("isGridView",true);
            updateLayout(isGridView);
            if (intent.getAction().equals("yihong.lrc")){

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(updateViewReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_local_music_list, null);
        gridView=layout;
        setupViews(layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_VIEW);
        updateViewReceiver=new UpdateViewReceiver();
        mActivity.registerReceiver(updateViewReceiver, intentFilter);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化界面
     * @param layout
     */
    private void setupViews(View layout) {
        mlinearLayout = layout.findViewById(R.id.control_panel);
        mfragmwntlinearLayout = layout.findViewById(R.id.v_local_up_listview);
        mlinearLayout.setVisibility(View.VISIBLE);
        mSlidingUpPanelLayout = layout.findViewById(R.id.sliding_layout);
        mSlidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if(slideOffset>0.8f && mlinearLayout!=null) {
                    //判断上滑快到顶部时隐藏
                    if(mlinearLayout.getVisibility()==View.VISIBLE) {
                        mlinearLayout.setVisibility(View.GONE);
                    }
                    hideViews();
                }else if(slideOffset<=0.3f && mlinearLayout!=null){
                    //判断上滑快到底部时显示
                    if(mlinearLayout.getVisibility()!=View.VISIBLE) {
                        mlinearLayout.setVisibility(View.VISIBLE);
                    }
                    showViews();
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
//                mlinearLayout.setVisibility(View.GONE);
//                mActivity.Visiable();import android.widget.FrameLayout.LayoutParams
            }

            @Override
            public void onPanelCollapsed(View panel) {
                showViews();
            }
        });
        mGridView=layout.findViewById(R.id.app_grid);
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
        mGridView.setOnItemClickListener(mMusicItemClickListener);
        mListView.setAdapter(adapter);
//        mGridView.setAdapter(adapter);
        mGridView.setVisibility(View.GONE);
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
     * 隐藏toolbar
     */
    private void hideViews() {

        mActivity.toolbar.animate().translationY(-mActivity.toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        mActivity.linearLayout.animate().translationY(-mActivity.linearLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        mActivity.mviewPager.animate().translationY(-mActivity.linearLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2));

    }


    /**
     * 显示toolbar
     */
    private void showViews() {

        mActivity.toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mActivity.linearLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mActivity.mviewPager.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));

    }


    /**
     * 更新grid view视图
     * @param isGrid
     */
    private void updateLayout(boolean isGrid) {
        if (isGrid) {
            if (mGridView == null)
            {
                mGridView = gridView.findViewById(R.id.app_grid);
            }
            mGridView.setVisibility(View.VISIBLE);
            gridadapter = new LocalMusicListAdapter(true);
            mGridView.setAdapter(gridadapter);
            mListView.setVisibility(View.GONE);
        } else {
            if (mListView == null)
            {
                mListView = gridView.findViewById(R.id.music_list_view);
            }
            listadapter = new LocalMusicListAdapter(false);
            mListView.setVisibility(View.VISIBLE);
            mListView.setAdapter(listadapter);
            mGridView.setVisibility(View.GONE);
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
            Log.e("Local",currentProgress);
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


    /**
     * 监听歌曲点击事件
     */
    private AdapterView.OnItemClickListener mMusicItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
                play(position);
            if (tagb==null) {
                currentAdd = position;
                realView = (BarChartView) viewList.get(position);
                realView.setVisibility(View.VISIBLE);
                realView.start();
                tagb="true";
            }else {
                if (currentAdd==position){
                    realView = (BarChartView) viewList.get(position);
                    realView.setVisibility(View.VISIBLE);
                    realView.start();
                }else {
                    realView = (BarChartView) viewList.get(currentAdd);
                    realView.setVisibility(View.GONE);
                    realView = (BarChartView) viewList.get(position);
                    realView.setVisibility(View.VISIBLE);
                    realView.start();
                    currentAdd=position;
                }
            }
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
        //TODO 播放歌词同步异常
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
            realView.start();
            playBtn.setImageResource(R.drawable.player_btn_pause_normal);
        } else {
            realView.stop();
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
                //替换为面板滑动
//                mActivity.Visiable();
//                fragmentManager=getFragmentManager();
//                transaction = fragmentManager.beginTransaction();
//                MusicDetailFragment musicDetailFragment=new MusicDetailFragment();
//                transaction.add(R.id.music_detail_fragment,musicDetailFragment).addToBackStack(null).commit();
                break;
        }
    }

    public class LocalMusicListAdapter extends BaseAdapter {
        private static final String TAG = "LocalMusicListAdapter";
        private View view;
        private boolean isGrid=false;
        public LocalMusicListAdapter(MainActivity mActivity) {

        }
        public LocalMusicListAdapter(boolean isView) {
            isGrid=isView;
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
                if (isGrid==false) {
                    convertView = View.inflate(AppliContext.sContext, R.layout.local_music_item, null);
                }else {
                convertView = View.inflate(AppliContext.sContext, R.layout.local_music_grid_item, null);
                }
                viewHolder = new ViewHolder();
                viewHolder.annotion=convertView.findViewById(R.id.local_rl_annotion);
                viewHolder.icon = convertView.findViewById(R.id.music_list_icon);
                viewHolder.title = convertView.findViewById(R.id.tv_music_list_title);
                viewHolder.artist = convertView.findViewById(R.id.tv_music_list_artist);
                viewHolder.menuImage=convertView.findViewById(R.id.iv_aplist_btn);
                viewHolder.mark = convertView.findViewById(R.id.music_list_selected);
                viewHolder.realMapView=convertView.findViewById(R.id.myRealMapView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            myListener=new MyListener(position);
            Music music = (Music) getItem(position);

//            Bitmap icon = BitmapFactory.decodeFile(music.getImage());
//            viewHolder.icon.setImageBitmap(icon == null ?
//                    BitmapFactory.decodeResource(
//                            AppliContext.sContext.getResources(), R.mipmap.img) : icon);
            Bitmap icon= MusicMemoryCacheUtils.getInstance().load(music.getImage());
            viewHolder.icon.setImageBitmap(icon==null? ImageUtils
                    .scaleBitmap(R.mipmap.img) : ImageUtils.scaleBitmap(icon));
            viewHolder.title.setText(music.getTitle());
            viewHolder.artist.setText(music.getArtist());
            viewHolder.menuImage.setOnClickListener(myListener);
            if (viewHolder.realMapView!=null) {
                viewHolder.realMapView.setVisibility(View.GONE);
                //TODO 设置后 视图切换报错
            }
            viewList.add(viewHolder.realMapView);
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
            BarChartView realMapView;
            RelativeLayout annotion;
        }
    }
}
