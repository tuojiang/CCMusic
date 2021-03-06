package oyh.ccmusic.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
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
import oyh.ccmusic.util.ImageUtils;
import oyh.ccmusic.util.MusicMemoryCacheUtils;
import oyh.ccmusic.util.MusicUtils;

import static oyh.ccmusic.util.MusicUtils.queryGenrestItem;

/**
 * 流派列表
 * A simple {@link Fragment} subclass.
 */
public class GenresMusicFragment extends Fragment {

    private MainActivity mActivity;
    private ListView mListView;
    private GridView mGridView;
    public static Boolean isGridView;
    private UpdateViewReceiver updateViewReceiver;
    private View gridView;
    private String UPDATE_VIEW="oyh.ccmusic.updateview";
    private GenresListAdapter adapter;
    private GenresListAdapter gridadapter;
    private GenresListAdapter listadapter;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;


    public GenresMusicFragment() {
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
        View layout = inflater.inflate(R.layout.fragment_genres_music, container, false);
        gridView=layout;
        mListView=layout.findViewById(R.id.lv_genres_music);
        mGridView=layout.findViewById(R.id.lv_genres_music_grid);
        adapter=new GenresListAdapter();
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
            gridadapter = new GenresListAdapter(true);
            mGridView.setAdapter(gridadapter);
            mListView.setVisibility(View.GONE);
        } else {
            if (mListView == null)
            {
                mListView = gridView.findViewById(R.id.lv_album_music);
            }
            listadapter = new GenresListAdapter(false);
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
            String genresName=MusicUtils.genreslList.get(position).getGenres();
            queryGenrestItem(genresName,MusicUtils.sMusicList);
            Log.e("queryGenrestItem", String.valueOf(MusicUtils.itemCommonList.size()));
            fragmentManager=getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            GenresItemFragment genresItemFragment=new GenresItemFragment();
            transaction.replace(R.id.music_detail_fragment,genresItemFragment).addToBackStack(null).commit();
        }
    };

    public class GenresListAdapter extends BaseAdapter{
        private boolean isGrid=false;
        class ViewHolder{
            ImageView ico;
            TextView title;
            TextView songView;
        }
        public GenresListAdapter(boolean isView) {
            isGrid=isView;
        }
        public GenresListAdapter() {
        }

        @Override
        public int getCount() {
            return MusicUtils.genreslList.size();
        }

        @Override
        public Object getItem(int i) {
            return MusicUtils.genreslList.get(i);
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
                view=View.inflate(AppliContext.sContext,R.layout.genres_music_item,null);
                }else {
                view=View.inflate(AppliContext.sContext,R.layout.genres_music_grid_item,null);
                }
                viewHolder=new ViewHolder();
                viewHolder.ico=view.findViewById(R.id.iv_genres_layout_ico);
                viewHolder.title=view.findViewById(R.id.tv_genres_list_genres);
                viewHolder.songView=view.findViewById(R.id.tv_genres_list_songs);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) view.getTag();
            }
            Music music= (Music) getItem(i);
            Bitmap icon = MusicMemoryCacheUtils.getInstance().load(music.getImage());
            viewHolder.ico.setImageBitmap(icon==null ? ImageUtils
                    .scaleBitmap(R.mipmap.img) : ImageUtils.scaleBitmap(icon));
//            Bitmap ico= BitmapFactory.decodeFile(music.getImage());
//            viewHolder.ico.setImageBitmap(ico==null?BitmapFactory.decodeResource(AppliContext.sContext.getResources(),R.mipmap.img):ico);
            viewHolder.title.setText(music.getGenres());
            String amount= MusicUtils.map.get(music.getGenres());
            String songs = String.format( AppliContext.sContext.getResources().getString( R.string.genres_songs_string), amount);
            viewHolder.songView.setText(songs);
            return view;
        }
    }
}
