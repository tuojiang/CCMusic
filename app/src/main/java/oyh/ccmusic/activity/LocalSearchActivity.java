package oyh.ccmusic.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

import oyh.ccmusic.R;
import oyh.ccmusic.util.MusicUtils;

public class LocalSearchActivity extends AppCompatActivity {
    private static final String TAG = LocalSearchActivity.class.getSimpleName();
    private static final int REQ_PERMISSION = 100;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private SearchView mSearchView;
    private Toolbar mToolbar;
    private ListView mLvMusic;
    private MenuItem searchItem;
    private ArrayList<String> localSearch;
    private LocalSearchActivity mActivity;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_local_search);

        initView();
        initToolbar();
        requestPermission();
    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mLvMusic = (ListView) findViewById(R.id.lv_music);
        mLvMusic.setOnItemClickListener(mSearchItemClickListener);

    }

    /**
     * 监听歌曲点击事件
     */
    private AdapterView.OnItemClickListener mSearchItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            String name = localSearch.get(position);
            int index = MusicUtils.queryNameToList(name);
            Intent data = new Intent();
            data.putExtra("index",index);
            setResult(1, data);
            finish();
            MusicUtils.put("searchps",index);
        }
    };

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchAutoComplete.isShown()) {
                    try {
                        mSearchAutoComplete.setText("");//清除文本
                        //利用反射调用收起SearchView的onCloseClicked()方法
                        Method method = mSearchView.getClass().getDeclaredMethod("onCloseClicked");
                        method.setAccessible(true);
                        method.invoke(mSearchView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                    Intent data = new Intent();
//                    setResult(2, data);
//                    MusicUtils.put("issearch",0);
//                    finish();
                    Intent intent = new Intent(LocalSearchActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //如果还没有读取SD卡的权限,申请
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_PERMISSION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);

        searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint("输入歌曲名查找");

        mSearchView.onActionViewExpanded();// 当展开无输入内容的时候，没有关闭的图标
        mSearchView.setIconified(true);//设置searchView处于展开状态

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //提交按钮的点击事件
                Toast.makeText(LocalSearchActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //当输入框内容改变的时候回调
                quertMusic(newText);
                return true;
            }
        });


        mSearchAutoComplete =  mSearchView.findViewById(R.id.search_src_text);

        //设置输入框内容文字和提示文字的颜色
        mSearchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
        mSearchAutoComplete.setTextColor(getResources().getColor(android.R.color.white));

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 模糊查找音乐
     * @param key
     */
    private void quertMusic(String key) {
        localSearch=MusicUtils.localSearchList;
        String[] musics = new String[]{};
        if (!TextUtils.isEmpty(key)){
            musics = MusicUtils.queryMusicName(this, key);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, musics);
        mLvMusic.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "读取SD卡权限被拒绝了", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
