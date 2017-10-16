package oyh.ccmusic.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.adapter.SearchMusic;
import oyh.ccmusic.adapter.SearchResultAdapter;
import oyh.ccmusic.domain.SearchResult;
import oyh.ccmusic.util.MobileUtils;
import oyh.ccmusic.util.MultipartThreadDownloador;
import oyh.ccmusic.util.MusicUtils;

/**
 * 网络歌曲列表
 * Created by yihong.ou on 17-9-18.
 */
public class NetMusicFragment extends Fragment implements View.OnClickListener{

    private MainActivity mActivity;
    private LinearLayout mSearchShowLinearLayout;
    private LinearLayout mSearchLinearLayout;
    private ImageButton mSearchButton;
    private EditText mSearchEditText;
    private ListView mSearchResultListView;
    private ProgressBar mSearchProgressBar;
    private TextView mFooterView;
    private View mPopView;
    private PopupWindow mPopupWindow;
    private String currentPos;         // 记录当前正在播放的音乐
    private String songLongId;         // 记录当前正在播放的音乐
    private MediaPlayer mPlayer=new MediaPlayer();
    private SearchResultAdapter mSearchResultAdapter;
    private ArrayList<SearchResult> mResultData = new ArrayList<SearchResult>();
    private int mPage = 0;
    private int mLastItem;
    private boolean hasMoreData = true;
    private String adress;//歌词地址
    private String songAdress;//歌曲地址
    private String file_link;
    private String lrc_link;
    private String songTitle;
    private String netLrc;
    private String getDate;
    private int file_duration,file_size;
    public NetMusicFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity= (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout= inflater.inflate(R.layout.fragment_net_music, null);
        initData(layout);
        return layout;
        //TODO 刷新奔溃问题待解决
    }

    /**
     * 初始化界面
     * @param layout
     */
    private void initData(View layout) {
        mSearchShowLinearLayout=layout.findViewById(R.id.ll_search_btn_container);
        mSearchLinearLayout=layout.findViewById(R.id.ll_search_container);
        mSearchButton=layout.findViewById(R.id.ib_search_btn);
        mSearchEditText=layout.findViewById(R.id.et_search_content);
        mSearchResultListView=layout.findViewById(R.id.lv_search_result);
        mSearchProgressBar=layout.findViewById(R.id.pb_search_wait);
        mFooterView=buildFooterView();

        mSearchShowLinearLayout.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

        mSearchResultListView.addFooterView(mFooterView);

        mSearchResultAdapter = new SearchResultAdapter(mResultData);
        mSearchResultListView.setAdapter(mSearchResultAdapter);
        mSearchResultListView.setOnScrollListener(mListViewScrollListener);
        mSearchResultListView.setOnItemClickListener(mResultItemClickListener);
        mSearchResultListView.setOnItemLongClickListener(longClickListener);

    }
    /**
     * 列表中每一列的点击时间监听器
     */
    private TextView buildFooterView() {
        TextView footerView = new TextView(mActivity);
        footerView.setText("加载下一页...");
        footerView.setGravity(Gravity.CENTER);
        footerView.setVisibility(View.GONE);

        return footerView;
    }

    /**
     * 长按事件监听
     */
    private AdapterView.OnItemLongClickListener longClickListener=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            String songid=mResultData.get(i).getUrl().substring(6);
            songLongId=songid;
            getDownloadUrl(songLongId);//获取歌曲文件的地址
//            getAdress(songLongId);//获取歌曲歌词的地址
            showDownloadDialog(i);
            return true;
        }
    };
    /**
     * 点击事件监听
     */
    private AdapterView.OnItemClickListener mResultItemClickListener = new AdapterView.OnItemClickListener() {
        @SuppressLint("LongLogTag")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if(position >= mResultData.size() || position < 0) return;
            String songid=mResultData.get(position).getUrl().substring(6);
            currentPos=songid;
            play(currentPos);


            Log.e("currentPos", "currentPos="+currentPos);
        }
    };


    /**
     * 底部对话框
     * @param position
     */
    private void showDownloadDialog(final int position) {
        mActivity.onPopupWindowShown();
        if(mPopupWindow == null) {
            mPopView = View.inflate(mActivity, R.layout.download_pop_layout, null);

            mPopupWindow = new PopupWindow(mPopView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupWindow.setAnimationStyle(R.style.popwin_anim);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
//                    mActivity.onPopupWindowDismiss();

                }
            });
        }

        mPopView.findViewById(R.id.tv_pop_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadSong();//歌曲下载
                downloadSongLrc();//歌词下载
                dismissDialog();
            }
        });
        mPopView.findViewById(R.id.tv_pop_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        /**
         * 设置对话框展示的位置
         */
        mPopupWindow.showAtLocation(mActivity.getWindow().getDecorView(),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void dismissDialog() {
        if(mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 歌词下载
     */
    private void downloadSongLrc(){
        final String appHome = Environment.getExternalStorageDirectory().getAbsolutePath()+"/CCMusic";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {

                    new MultipartThreadDownloador(lrc_link,
                            appHome, songTitle+".lrc", 2).download();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 歌曲下载
     */
    private void downloadSong(){
        final String appHome = Environment.getExternalStorageDirectory().getAbsolutePath()+"/CCMusic";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    new MultipartThreadDownloador(file_link,
                            appHome, songTitle+".mp3", 2).download();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取要下载的歌曲URL
     * @param songid
     */
    private void getDownloadUrl(final String songid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection;
                    URL url = new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=baidu.ting.song.play&songid="+songid);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(60*1000);
                    connection.setReadTimeout(60*1000);
                    connection.connect();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String s;
                    while ((s=reader.readLine())!=null){
                        s = s.replace("\\","");//去掉\\
                        s = s.replace("\\n","");//去掉换行符
                        s = s.replace("\"0\":\"129|-1\",\"1\":\"-1|-1\"","\\\"0\\\":\\\"0|0\\\",\\\"1\\\":\\\"0|0\\\"");//修正json格式
                        getDate=s;
                        Log.e("s", "s="+s);
                    }
                    try {
                        JSONObject object = new JSONObject(getDate);
                        JSONObject bitrateJSON = object.getJSONObject("bitrate");
                        JSONObject songinfoJSON = object.getJSONObject("songinfo");
                        file_link = bitrateJSON.getString("file_link");
                        lrc_link=songinfoJSON.getString("lrclink");
                        MusicUtils.put("filesize",bitrateJSON.getString("file_size"));
                        songTitle=songinfoJSON.getString("title");
                        Log.e("getDownloadUrl", "lrc_link="+lrc_link+"getDownloadUrl="+file_link);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 播放点击歌曲并保存当前位置值
     * @param songid
     */
    private void play(final String songid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection;
                    URL url = new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=baidu.ting.song.play&songid="+songid);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(60*1000);
                    connection.setReadTimeout(60*1000);
                    connection.connect();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String s;
                    while ((s=reader.readLine())!=null){
                        s = s.replace("\\","");//去掉\\
                        s = s.replace("\\n","");//去掉换行符
                        s = s.replace("\"0\":\"129|-1\",\"1\":\"-1|-1\"","\\\"0\\\":\\\"0|0\\\",\\\"1\\\":\\\"0|0\\\"");//修正json格式
                        getDate=s;
                        Log.e("s", "s="+s);
                    }
                    try {
                        JSONObject object = new JSONObject(getDate);
                        JSONObject bitrateJSON = object.getJSONObject("bitrate");
                        file_link = bitrateJSON.getString("file_link");
                        Log.e("s", "s="+file_link);
                        file_size = bitrateJSON.optInt("file_size");
                        file_duration = bitrateJSON.optInt("file_duration");
                        mActivity.getLocalMusicService().playNet(file_link);
                        Toast.makeText(mActivity,"音乐加载中,请稍等...",Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private AbsListView.OnScrollListener mListViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mLastItem == mSearchResultAdapter.getCount() && hasMoreData
                    && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                String searchText = mSearchEditText.getText().toString().trim();
                if(TextUtils.isEmpty(searchText)) return;

                mFooterView.setVisibility(View.VISIBLE);
                startSearch(searchText);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            mLastItem = firstVisibleItem + visibleItemCount - 1;
        }
    };
    private void search() {
        MobileUtils.hideInputMethod(mSearchEditText);
        String content = mSearchEditText.getText().toString().trim();
        if(TextUtils.isEmpty(content)) {
            Toast.makeText(mActivity, "请输入关键词", Toast.LENGTH_SHORT).show();
            return;
        }else {
            Toast.makeText(mActivity,"已开始查询，请耐心等待",Toast.LENGTH_SHORT).show();
        mPage = 0;
        mSearchProgressBar.setVisibility(View.VISIBLE);
        mSearchResultListView.setVisibility(View.GONE);
            startSearch(content);
//            query2(content);
        }

    }
//    这种方式也可以解析得到相应数据
//    private void query2(final String title){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    HttpURLConnection connection;
//                    String finalTitle = URLEncoder.encode(title,"utf-8");
//                    URL url = new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.search.catalogSug&format=json&callback=&query="+finalTitle);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setConnectTimeout(60*1000);
//                    connection.setReadTimeout(60*1000);
//                    connection.connect();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    String s;
//                    if ((s=reader.readLine())!=null)
//                        Log.e("s", "s="+s);
//                    doJson(s);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//
//    public SearchResult doJson(String json){
//        SearchResult song = null;
//        JSONObject jsonObject = null;
//        try {
//            //去掉括号
//            json = json.replace("(","");
//            json = json.replace(")","");
//            jsonObject = new JSONObject(json);
//            JSONArray array = new JSONArray(jsonObject.getString("song"));
//            for (int i=0;i<array.length();i++){
//                JSONObject object = array.getJSONObject(i);
//                String songname = object.getString("songname");
//                String artistname = object.getString("artistname");
//                String songid = object.getString("songid");
//                currentPos =songid;
//                String adress = getAdress(songid);
//                SearchResult song1 = new SearchResult();
//                Log.e("tag",songname+"  "+artistname+"  "+songid);
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return song;
//    }

    /**
     * 获取歌词地址
     * @param songid
     * @return
     */
//    public void getAdress(final String songid){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    HttpURLConnection connection;
//                    URL url = new URL("http://ting.baidu.com/data/music/links?songIds="+songid);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setConnectTimeout(60*1000);
//                    connection.setReadTimeout(60*1000);
//                    connection.connect();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    String s;
//                    if ((s=reader.readLine())!=null){
//                        s = s.replace("\\","");//去掉\\
//                        try {
//                            JSONObject object = new JSONObject(s);
//                            JSONObject object1 = object.getJSONObject("data");
//                            JSONArray array = object1.getJSONArray("songList");
//                            JSONObject object2 = array.getJSONObject(0);
//                            adress = object2.getString("songLink");
//                            Log.e("tagadress","adress="+adress);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
    /**
     * 歌曲搜索解析
     * @param content
     */
    private void startSearch(String content) {
        SearchMusic.getInstance().setListener(new SearchMusic.OnSearchResultListener() {
            @Override
            public void onSearchResult(ArrayList<SearchResult> results) {
                if(mPage == 1) {
                    hasMoreData = true;
                    mSearchProgressBar.setVisibility(View.GONE);
                    mSearchResultListView.setVisibility(View.VISIBLE);
                }

                mFooterView.setVisibility(View.GONE);
                if(results == null || results.isEmpty()) {
                    hasMoreData = false;
                    return;
                }

                if(mPage == 1) mResultData.clear();

                mResultData.addAll(results);
                mSearchResultAdapter.notifyDataSetChanged();
            }
        }).search(content, ++mPage);
    }
        @Override
    public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_search_btn_container:
                    mSearchShowLinearLayout.setVisibility(View.GONE);
                    mSearchLinearLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.ib_search_btn:
                    mSearchShowLinearLayout.setVisibility(View.VISIBLE);
                    mSearchLinearLayout.setVisibility(View.GONE);
                    search();
                    break;
            }
    }
}
