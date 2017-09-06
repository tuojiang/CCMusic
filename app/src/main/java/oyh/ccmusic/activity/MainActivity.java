package oyh.ccmusic.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import oyh.ccmusic.R;
import oyh.ccmusic.adapter.MFragmentPagerAdapter;
import oyh.ccmusic.fragment.LocalMusicFragment;
import oyh.ccmusic.fragment.MloveMusicFragment;
import oyh.ccmusic.fragment.NetMusicFragment;

public class MainActivity extends AppCompatActivity {

    private TextView localMTextview;
    private TextView netMTextview;
    private TextView myloveMTextview;

    private ImageView cursor;
    //图片偏移量
    private int offset = 0;
    private int position_one;
    private int position_two;
    //图片宽度
    private int bmpW;

    private ViewPager mviewPager;

    private int currentIndex;
    private ArrayList<Fragment> fragmentArrayList;
    private FragmentManager fragmentManager;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //初始化TextView
        InitTextView();

        //初始化Fragment
        InitFragment();

        //初始化ViewPager
        InitViewPager();

        //初始化InitImageView
        InitImageView();

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
        bmpW = (screenW/3);
        //设置游标宽度
        setBmpW(cursor, bmpW);
        offset = 0;
        //游标偏移量赋值
        position_one = (int) (screenW / 3.0);
        position_two = position_one * 2;
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
        mviewPager= (ViewPager) findViewById(R.id.vPager);
        mviewPager.setAdapter(new MFragmentPagerAdapter(fragmentManager,fragmentArrayList));
        mviewPager.setOffscreenPageLimit(2);
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
        netMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        myloveMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color));

    }

    /**
     * 初始化Fragment
     */
    private void InitFragment() {
        fragmentArrayList=new ArrayList<>();
        fragmentArrayList.add(new LocalMusicFragment());
        fragmentArrayList.add(new NetMusicFragment());
        fragmentArrayList.add(new MloveMusicFragment());
        fragmentManager=getSupportFragmentManager();
    }
    /**
     * 初始化标题栏
     */
    private void InitTextView() {
        localMTextview= (TextView) findViewById(R.id.localmusic_tv);
        netMTextview= (TextView) findViewById(R.id.netmusic_tv);
        myloveMTextview= (TextView) findViewById(R.id.mylovemusic_tv);

        localMTextview.setOnClickListener(new MyOnClickListener(0));
        netMTextview.setOnClickListener(new MyOnClickListener(1));
        myloveMTextview.setOnClickListener(new MyOnClickListener(2));
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
                        animation = new TranslateAnimation(position_two, 0, 0, 0);
                        resetTextViewTextColor();
                        localMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 1:
                    if (currentIndex==0){
                        animation = new TranslateAnimation(offset, position_one, 0, 0);
                        resetTextViewTextColor();
                        netMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }else if (currentIndex==2){
                        animation = new TranslateAnimation(position_two, position_one, 0, 0);
                        resetTextViewTextColor();
                        netMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 2:
                    if (currentIndex==0){
                        animation = new TranslateAnimation(offset, position_two, 0, 0);
                        resetTextViewTextColor();
                        myloveMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }else if (currentIndex==1){
                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
                        resetTextViewTextColor();
                        myloveMTextview.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
            }
            currentIndex=position;
            animation.setFillAfter(true);// true:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
