package oyh.ccmusic.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import oyh.ccmusic.Provider.PlayListContentProvider;
import oyh.ccmusic.R;
import oyh.ccmusic.adapter.MFragmentPagerAdapter;
import oyh.ccmusic.fragment.AlbumMusicFragment;
import oyh.ccmusic.fragment.ArtistMusicFragment;
import oyh.ccmusic.fragment.GenresMusicFragment;
import oyh.ccmusic.fragment.LocalMusicFragment;
import oyh.ccmusic.fragment.MloveMusicFragment;
import oyh.ccmusic.fragment.NetMusicFragment;
import oyh.ccmusic.service.LocalMusicService;
import oyh.ccmusic.util.MusicUtils;

/**
 * Created by yihong.ou on 17-9-7.
 */
public class MainActivity extends FragmentActivity {

    private MainActivity mActivity;
    private TextView localMTextview;
    private TextView netMTextview;
    private TextView myloveMTextview;
    private TextView albumTextview;
    private TextView artistTextview;
    private TextView genresTextview;
    private ImageView darwerImageView;
    public LinearLayout linearLayout;
    public LinearLayout linearLayout1;
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    private ImageView cursor;
    private Toolbar toolbar;
    private TextView toolbarsearch;
    //图片偏移量
    private int offset = 0;
    private int position_one;
    private int position_two;
    //图片宽度
    private int bmpW;
    private Handler mHand = null;
    private ViewPager mviewPager;
    private ContentObserver mDatabaseListener = null;
    private int currentIndex;
    private ArrayList<Fragment> fragmentArrayList;
    private FragmentManager fragmentManager;
    public static Context context;
    private android.support.v4.app.FragmentTransaction transaction;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("我的乐库");//设置主标题
        //绑定服务
        bindService(new Intent(this, LocalMusicService.class), localplayServiceConnection,
                Context.BIND_AUTO_CREATE);
        //初始化监听
        observer();

        //初始化Fragment
        InitFragment();

        //初始化TextView
        InitTextView();

        //初始化ViewPager
        InitViewPager();

        //初始化InitImageView
        InitImageView();

        //权限获取
//        verifyStoragePermissions(this);

        //注册广播
        registerReceiver();
        //初始化专辑列表
        MusicUtils.initAlbumList(this,MusicUtils.commonList);
        //初始化艺术家列表
        MusicUtils.initArtistList(this,MusicUtils.commonList);
        //初始化流派列表
        MusicUtils.initGenresList(this,MusicUtils.commonList);

    }
    private LocalMusicService.CallBack callBack;
    private ServiceConnection localplayServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            callBack= (LocalMusicService.CallBack) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            callBack=null;
        }
    };

    private void observerHeadSet(){

    }

    /**
     * 初始化监听
     */
    private void observer(){
        mHand = new Handler();
        // 数据库变动时的回调
        mDatabaseListener = new ContentObserver(mHand) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                MusicUtils.removeMusicSQLList(AppliContext.sContext);
                MusicUtils.initMusicSQLList(AppliContext.sContext);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
            }
        };
        // 注册数据库的监听，对应的是特定的Uri
        getContentResolver().registerContentObserver(PlayListContentProvider.CONTENT_SONGS_URI, true, mDatabaseListener);
        mHand.post(new Runnable()
        {
            public void run()
            {

            }
        });
    }


    /**
     * Fragment的view加载完成后回调
     */
    public void allowBindService() {
        bindService(new Intent(this, LocalMusicService.class), localplayServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    /**
     * fragment的view消失后回调
     */
    public void allowUnbindService() {
        unbindService(localplayServiceConnection);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");
        registerReceiver(mScanSDCardReceiver, filter);

        IntentFilter headSetFilter = new IntentFilter();
        headSetFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        headSetFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mHeadSetReceiver, headSetFilter);
    }
    /**
     * 获取音乐播放服务
     * @return
     */
    public LocalMusicService.CallBack getLocalMusicService() {

        return callBack;
    }


    /**
     * 初始化页卡游标
     */
    private void InitImageView() {
        cursor= (ImageView) findViewById(R.id.cursor);
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 获取分辨率宽度
        int screenW = dm.widthPixels;
//        bmpW = (screenW/3);
        bmpW = (screenW/6);
        //设置游标宽度
        setBmpW(cursor, bmpW);
        offset = 0;
        //游标偏移量赋值
//        position_one = (int) (screenW / 3.0);
        position_one = (int) (screenW / 6.0);
//        position_two = position_one * 2;
        position_two = position_one * 5;
    }
    /**
     * 设置游标宽度
     * @param mWidth
     */
    private void setBmpW(ImageView imageView, int mWidth) {
        ViewGroup.LayoutParams para;
        para = imageView.getLayoutParams();
        para.width = mWidth;
        imageView.setLayoutParams(para);
    }

    /**
     * 初始化页卡内容区
     */
    private void InitViewPager() {
        mviewPager= findViewById(R.id.vPager);
        mviewPager.setAdapter(new MFragmentPagerAdapter(fragmentManager,fragmentArrayList));
        mviewPager.setOffscreenPageLimit(5);
        mviewPager.setCurrentItem(0);
        resetTextViewTextColor();
        localMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
        mviewPager.addOnPageChangeListener(new MyOnPageChangeListener());

    }
    /**
     * 讲标题栏文字恢复默认值
     */
    private void resetTextViewTextColor() {
        localMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        albumTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        artistTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        genresTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        netMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        myloveMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color));

    }

    /**
     * 初始化Fragment
     */
    private void InitFragment() {
        fragmentArrayList=new ArrayList<>();
        fragmentArrayList.add(new LocalMusicFragment());
        fragmentArrayList.add(new AlbumMusicFragment());
        fragmentArrayList.add(new ArtistMusicFragment());
        fragmentArrayList.add(new MloveMusicFragment());
        fragmentArrayList.add(new GenresMusicFragment());
        fragmentArrayList.add(new NetMusicFragment());
        fragmentManager=getSupportFragmentManager();
    }
    public void Visiable(){
        linearLayout.setVisibility(View.GONE);
        linearLayout1.setVisibility(View.GONE);
    }
    /**
     * 初始化标题栏
     */
    private void InitTextView() {
//        搜索
        toolbarsearch=findViewById(R.id.tv_toolbar_search);
        toolbarsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicUtils.initSearchList(AppliContext.sContext);
                Log.e("onClick","onClick="+MusicUtils.localSearchList.size());
                Intent intent=new Intent(MainActivity.this,LocalSearchActivity.class);
                startActivity(intent);
            }
        });
        drawerLayout = findViewById(R.id.drawer_layout);
        darwerImageView=findViewById(R.id.iv_darwer_btn);
        darwerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView=findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_mymusic:
                        mviewPager.setCurrentItem(0);
                        toolbar.setTitle("我的乐库");
                        break;
                    case R.id.item_design:
                        Intent intent=new Intent(MainActivity.this,VersionActivity.class);
                        startActivity(intent);
                        break;
                }
                item.setChecked(true);//点击了把它设为选中状态
                drawerLayout.closeDrawers();//关闭抽屉
                return true;
            }
        });
        linearLayout=findViewById(R.id.linearLayout1);
        linearLayout1=findViewById(R.id.linearLayout2);
        context=getApplicationContext();

        localMTextview= findViewById(R.id.localmusic_tv);
        albumTextview=findViewById(R.id.albummusic_tv);
        artistTextview=findViewById(R.id.artistmusic_tv);
        myloveMTextview= findViewById(R.id.mylovemusic_tv);
        genresTextview=findViewById(R.id.genresmusic_tv);
        netMTextview= findViewById(R.id.netmusic_tv);

        localMTextview.setOnClickListener(new MyOnClickListener(0));
        albumTextview.setOnClickListener(new MyOnClickListener(1));
        artistTextview.setOnClickListener(new MyOnClickListener(2));
        myloveMTextview.setOnClickListener(new MyOnClickListener(3));
        genresTextview.setOnClickListener(new MyOnClickListener(4));
        netMTextview.setOnClickListener(new MyOnClickListener(5));
    }

    public void onPopupWindowShown() {

    }

    /**
     * 设置标题栏监听
     */
    public class MyOnClickListener implements View.OnClickListener{
        private int index=0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View view) {
            mviewPager.setCurrentItem(index);
        }
    }
    @Override
    protected void onResume() {
        /**
         * 设置为竖屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }
    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Animation animation = null;
            switch (position){
                case 0:
                    if (currentIndex==1){
                        animation = new TranslateAnimation(position_one, 0, 0, 0);
                        resetTextViewTextColor();
                        localMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }else if (currentIndex==2){
                        animation = new TranslateAnimation(position_one, 0, 0, 0);
                        resetTextViewTextColor();
                        localMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 1:
                    if (currentIndex==0){
                        animation = new TranslateAnimation(offset, position_one, 0, 0);
                        resetTextViewTextColor();
                        albumTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }else if (currentIndex==2){
                        animation = new TranslateAnimation(position_one*2, position_one, 0, 0);
                        resetTextViewTextColor();
                        albumTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 2:
                    if (currentIndex==1){
                        animation = new TranslateAnimation(position_one, position_one*2, 0, 0);
                        resetTextViewTextColor();
                        artistTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }else if (currentIndex==3){
                        animation = new TranslateAnimation(position_one*3, position_one*2, 0, 0);
                        resetTextViewTextColor();
                        artistTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 3:
                    if (currentIndex==2){
                        animation = new TranslateAnimation(position_one*2, position_one*3, 0, 0);
                        resetTextViewTextColor();
                        myloveMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }else if (currentIndex==4){
                        animation = new TranslateAnimation(position_one*4, position_one*3, 0, 0);
                        resetTextViewTextColor();
                        myloveMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 4:
                    if (currentIndex==3){
                        animation = new TranslateAnimation(position_one*3, position_one*4, 0, 0);
                        resetTextViewTextColor();
                        genresTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }else if (currentIndex==5) {
                        animation = new TranslateAnimation(position_one*5, position_one*4, 0, 0);
                        resetTextViewTextColor();
                        genresTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 5:
                    if (currentIndex==4){
                        animation = new TranslateAnimation(position_one*4, position_one*5, 0, 0);
                        resetTextViewTextColor();
                        netMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }else if (currentIndex==0) {
                        animation = new TranslateAnimation(position_two, position_one*5, 0, 0);
                        resetTextViewTextColor();
                        netMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;

            }
            currentIndex=position;
            if(animation!=null) {
                animation.setFillAfter(true);// true:图片停在动画结束位置
                animation.setDuration(300);
                cursor.startAnimation(animation);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mScanSDCardReceiver);
        unregisterReceiver(mHeadSetReceiver);
        super.onDestroy();

    }
    /**
     * 注册耳机拔插的广播接收者
     */
    private BroadcastReceiver mHeadSetReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (BluetoothProfile.STATE_DISCONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                    //蓝牙耳机现在断开连接
                }
            } else if ("android.intent.action.HEADSET_PLUG".equals(action)) {
                if (intent.hasExtra("state")){
                    if (intent.getIntExtra("state", 0) == 0){
                        //耳机拔出
                        Toast.makeText(context, "耳机拔出", Toast.LENGTH_LONG).show();
                        if (callBack!=null) {
                            callBack.isPlayerMusic();
                        }
                    }else if (intent.getIntExtra("state", 0) == 1){
                        //耳机插入
                        Toast.makeText(context, "耳机插入", Toast.LENGTH_LONG).show();
                        if (callBack!=null) {
                            callBack.isPlayerMusic();
                        }
                    }
                }
            }
        }
    };

    /**
     * 注册扫描完毕的广播接收者
     */
    private BroadcastReceiver mScanSDCardReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                Log.e("MainActivity","receiver");
                ((LocalMusicFragment)fragmentArrayList.get(0)).onMusicListChanged();
                Log.e("MainActivity","receiver"+fragmentArrayList.size());
            }
        }
    };

    /**
     * 权限获取
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
