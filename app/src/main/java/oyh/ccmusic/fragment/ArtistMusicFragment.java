package oyh.ccmusic.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

import static oyh.ccmusic.util.MusicUtils.queryArtistItem;

/**
 * 艺术家列表
 * A simple {@link Fragment} subclass.
 */
public class ArtistMusicFragment extends Fragment {

    private ListView mListView;
    private GridView mGridView;
    public static Boolean isGridView;
    private UpdateViewReceiver updateViewReceiver;
    private View gridView;
    private String UPDATE_VIEW="oyh.ccmusic.updateview";
    private MainActivity mActivity;
    private ArtistListAdapter adapter;
    private ArtistListAdapter gridadapter;
    private ArtistListAdapter listadapter;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;
    public ArtistMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity= (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_artist_music, null);
        gridView=layout;
        adapter=new ArtistListAdapter(mActivity);
        mListView=layout.findViewById(R.id.lv_artist_music);
        mGridView=layout.findViewById(R.id.lv_artist_music_grid);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mMusicItemClickListener);
        mGridView.setOnItemClickListener(mMusicItemClickListener);
        mGridView.setVisibility(View.GONE);
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
            gridadapter = new ArtistListAdapter(true);
            mGridView.setAdapter(gridadapter);
            mListView.setVisibility(View.GONE);
        } else {
            if (mListView == null)
            {
                mListView = gridView.findViewById(R.id.lv_album_music);
            }
            listadapter = new ArtistListAdapter(false);
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
            // 先把所属专辑下歌曲查询好放入itemCommonList数据库中备用
            String artist=MusicUtils.artistlList.get(position).getArtist();
            queryArtistItem(artist,MusicUtils.sMusicList);
            fragmentManager=getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            ArtistItemFragment artistItemFragment=new ArtistItemFragment();
            transaction.add(R.id.music_detail_fragment,artistItemFragment).addToBackStack(null).commit();
        }
    };


    public class ArtistListAdapter extends BaseAdapter{
        private boolean isGrid=false;
        private LayoutInflater inflater;
        class ViewHolder{
            ImageView icoView;
            TextView artistView;
            TextView albumAndSongView;
        }
        public ArtistListAdapter(boolean isView) {
            isGrid=isView;
        }
        public ArtistListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return MusicUtils.artistlList.size();
        }

        @Override
        public Object getItem(int i) {
            return MusicUtils.artistlList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view==null){
                if (isGrid==false) {
                    view=View.inflate(AppliContext.sContext,R.layout.artist_music_item,null);
                }else {
                    view=View.inflate(AppliContext.sContext,R.layout.artist_music_grid_item,null);
                }

                viewHolder=new ViewHolder();
                viewHolder.icoView=view.findViewById(R.id.iv_artist_layout_ico);
                viewHolder.artistView=view.findViewById(R.id.tv_artist_list_artist);
                viewHolder.albumAndSongView=view.findViewById(R.id.tv_artist_list_songs_and_albums);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) view.getTag();
            }
            Music music= (Music) getItem(i);
            Bitmap ico= BitmapFactory.decodeFile(music.getImage());
            viewHolder.icoView.setImageBitmap(ico==null?BitmapFactory.decodeResource(AppliContext.sContext.getResources(),R.mipmap.img):ico);
            String songsAndAlbum =String.format(AppliContext.sContext.getResources().getString(R.string.artist_songs_albums_string),music.getArtistSongs(),music.getArtistAlbums());
            viewHolder.albumAndSongView.setText(songsAndAlbum);
            viewHolder.artistView.setText(music.getArtist());
            return view;
        }
    }

}
