package oyh.ccmusic.fragment;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.adapter.SearchMusic;
import oyh.ccmusic.adapter.SearchResultAdapter;
import oyh.ccmusic.domain.SearchResult;
import oyh.ccmusic.util.MobileUtils;

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

    private SearchResultAdapter mSearchResultAdapter;
    private ArrayList<SearchResult> mResultData = new ArrayList<SearchResult>();

    private int mPage = 0;
    private int mLastItem;
    private boolean hasMoreData = true;

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
        View layout= inflater.inflate(R.layout.fragment_net_music, container, false);
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
    private AdapterView.OnItemClickListener mResultItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if(position >= mResultData.size() || position < 0) return;

//			String url = mResultData.get(position).getUrl();
//			Intent intent = new Intent(mActivity, MusicInfoActivity.class);
//			intent.putExtra("url", url);
//			startActivity(intent);
//            showDownloadDialog(position);
        }
    };

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
//        MobileUtils.hideInputMethod(mSearchEditText);
        String content = mSearchEditText.getText().toString().trim();
        if(TextUtils.isEmpty(content)) {
            Toast.makeText(mActivity, "请输入关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        mPage = 0;
        mSearchProgressBar.setVisibility(View.VISIBLE);
        mSearchResultListView.setVisibility(View.GONE);

        startSearch(content);
    }

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
