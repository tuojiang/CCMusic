package oyh.ccmusic.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import java.util.ArrayList;

import oyh.ccmusic.R;
import oyh.ccmusic.adapter.LocalSearchAdapter;
import oyh.ccmusic.util.MusicUtils;

public class LocalSearchActivity extends AppCompatActivity {

    private SearchView searchview;
    private ListView listview;
    private ArrayList<String> data;
    private LocalSearchAdapter mAdapter;
    private Cursor mCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_search);
        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("音乐搜索");


    }

    private void findView() {
        searchview=(SearchView) findViewById(R.id.sv_local_music);
        listview=(ListView) findViewById(R.id.lv_search_local);
        listview.setTextFilterEnabled(true);//设置对字符串过滤 对应适配器
        searchview.setIconifiedByDefault(true); //表示搜索图标是否在输入框内。true效果更加
        searchview.onActionViewExpanded(); //表示在内容为空时不显示取消的x按钮，内容不为空时显示.
        searchview.setSubmitButtonEnabled(true);//编辑框后显示search按钮
        searchview.setFocusable(false);
        searchview.clearFocus();
        data = new ArrayList<>();
        mAdapter=new LocalSearchAdapter(this, data);
        listview.setAdapter(mAdapter);
        final ArrayList<String> nameList=MusicUtils.localSearchList;
		/*输入框文字listener*/
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {// 监听 SearchView 中的数据变化

            /*开始搜索listener*/
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                data=MusicUtils.localSearchList;
                mAdapter.notifyDataSetChanged();
                return true;
            }
            /*搜索变化listener*/
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!=null&&newText.length()>0){
                    listview.setFilterText(newText);
                }else{
                    listview.clearTextFilter();
                }
                if (searchview!= null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(
                                searchview.getWindowToken(), 0);
                    }
                    searchview.clearFocus();
                }
                return true;
            }
        });
		/*点击取消按钮listener，默认点击搜索输入框*/
        searchview.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                return true;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
