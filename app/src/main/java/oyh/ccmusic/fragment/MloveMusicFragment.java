package oyh.ccmusic.fragment;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.adapter.MyloveMusicListAdapter;
import oyh.ccmusic.util.MusicUtils;

import static oyh.ccmusic.util.MusicUtils.queryMLoveToList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MloveMusicFragment extends Fragment{

    private MainActivity mActivity;
    private ListView mListView;
    private GridView mGridView;
    public static Boolean isGridView;
    private UpdateViewReceiver updateViewReceiver;
    private View gridView;
    private String UPDATE_VIEW="oyh.ccmusic.updateview";
    private MyloveMusicListAdapter adapter;
    private MyloveMusicListAdapter gridadapter;
    private MyloveMusicListAdapter listadapter;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;

    public MloveMusicFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity= (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_mlove_music, null);
        gridView=layout;
        MusicUtils.initMusicSQLList(mActivity);
        adapter=new MyloveMusicListAdapter(MusicUtils.sMusicSQlList);
        mListView=layout.findViewById(R.id.music_mllist_view);
        mGridView=layout.findViewById(R.id.music_mllist_view_grid);
        mListView.setOnItemClickListener(mMusicItemClickListener);
        mGridView.setOnItemClickListener(mMusicItemClickListener);
        mGridView.setVisibility(View.GONE);
        mListView.setAdapter(adapter);
//        setupViews(layout);
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

    private class UpdateViewReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isGridView=intent.getBooleanExtra("isGridView",true);
            updateLayout(isGridView);
        }
    }

    private void updateLayout(boolean isGrid) {
        if (isGrid) {
            if (mGridView == null)
            {
                mGridView = gridView.findViewById(R.id.lv_album_music_grid);
            }
            mGridView.setVisibility(View.VISIBLE);
            gridadapter = new MyloveMusicListAdapter(true);
            mGridView.setAdapter(gridadapter);
            mListView.setVisibility(View.GONE);
        } else {
            if (mListView == null)
            {
                mListView = gridView.findViewById(R.id.lv_album_music);
            }
            listadapter = new MyloveMusicListAdapter(false);
            mListView.setVisibility(View.VISIBLE);
            mListView.setAdapter(listadapter);
            mGridView.setVisibility(View.GONE);
        }
    }

    /**
     * 监听歌曲点击事件
     */
    private AdapterView.OnItemClickListener mMusicItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            onPlay(position);
            mActivity.Visiable();
            fragmentManager=getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            MusicMLDetailFragment musicMLDetailFragment=new MusicMLDetailFragment();
            transaction.add(R.id.music_detail_fragment,musicMLDetailFragment).addToBackStack(null).commit();
        }
    };
    private void onPlay(int position){
        int index=queryMLoveToList(position);
        MusicUtils.put("index", index);
        mActivity.getLocalMusicService().mlovePlay(index,position);
    }

}
