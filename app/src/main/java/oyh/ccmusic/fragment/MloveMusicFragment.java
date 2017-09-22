package oyh.ccmusic.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.adapter.MyloveMusicListAdapter;
import oyh.ccmusic.util.MusicUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MloveMusicFragment extends Fragment{

    private MainActivity mActivity;
    private ListView mListView;
    private MyloveMusicListAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    public MloveMusicFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity= (MainActivity) getActivity();
    }
    public void onMusicMyLoveListChanged() {
        adapter.notifyDataSetChanged();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_mlove_music, null);
        MusicUtils.initMusicSQLList(mActivity);
        adapter=new MyloveMusicListAdapter(MusicUtils.sMusicSQlList);
        mListView=layout.findViewById(R.id.music_mllist_view);
        mListView.setOnItemClickListener(mMusicItemClickListener);
        mListView.setAdapter(adapter);
//        setupViews(layout);
        return layout;
    }

    /**
     * 监听歌曲点击事件
     */
    private AdapterView.OnItemClickListener mMusicItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Toast.makeText(AppliContext.sContext,"pisition:"+position,Toast.LENGTH_LONG).show();
        }
    };

}
