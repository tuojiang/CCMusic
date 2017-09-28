package oyh.ccmusic.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

import static oyh.ccmusic.util.MusicUtils.queryGenrestItem;

/**
 * 流派列表
 * A simple {@link Fragment} subclass.
 */
public class GenresMusicFragment extends Fragment {

    private MainActivity mActivity;
    private ListView mListView;
    private GenresListAdapter adapter;
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
        mListView=layout.findViewById(R.id.lv_genres_music);
        adapter=new GenresListAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mMusicItemClickListener);
        return layout;
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
            transaction.add(R.id.music_detail_fragment,genresItemFragment).addToBackStack(null).commit();
        }
    };

    public class GenresListAdapter extends BaseAdapter{
        class ViewHolder{
            ImageView ico;
            TextView title;
            TextView songView;
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
                view=View.inflate(AppliContext.sContext,R.layout.genres_music_item,null);
                viewHolder=new ViewHolder();
                viewHolder.ico=view.findViewById(R.id.iv_genres_layout_ico);
                viewHolder.title=view.findViewById(R.id.tv_genres_list_genres);
                viewHolder.songView=view.findViewById(R.id.tv_genres_list_songs);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) view.getTag();
            }
            Music music= (Music) getItem(i);
            Bitmap ico= BitmapFactory.decodeFile(music.getImage());
            viewHolder.ico.setImageBitmap(ico==null?BitmapFactory.decodeResource(AppliContext.sContext.getResources(),R.mipmap.img):ico);
            viewHolder.title.setText(music.getGenres());
            String amount= MusicUtils.map.get(music.getGenres());
            //TODO 获取流派歌曲数量
            String songs = String.format( AppliContext.sContext.getResources().getString( R.string.genres_songs_string), amount);
            viewHolder.songView.setText(songs);
            return view;
        }
    }
}