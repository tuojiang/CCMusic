package oyh.ccmusic.fragment;


import android.content.Context;
import android.content.Intent;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.adapter.AlbumMusicListAdapter;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

import static oyh.ccmusic.util.MusicUtils.queryItem;


/**
 * 专辑列表
 * A simple {@link Fragment} subclass.
 */
public class AlbumMusicFragment extends Fragment {

    private MainActivity mActivity;
    private ListView mListView;
    private AlbumListAdapter adapter;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;
    public AlbumMusicFragment() {
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
        View layout = inflater.inflate(R.layout.fragment_album_music, null);
        adapter=new AlbumListAdapter(mActivity);
        mListView=layout.findViewById(R.id.lv_album_music);
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
            String albumN=MusicUtils.albumlList.get(position).getAlbumName();
            queryItem(albumN,MusicUtils.sMusicList);
            fragmentManager=getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            AlbumItemFragment albumItemFragment=new AlbumItemFragment();
            transaction.add(R.id.music_detail_fragment,albumItemFragment).addToBackStack(null).commit();
        }
    };

    public class AlbumListAdapter extends BaseAdapter{

        private LayoutInflater inflater;
        class ViewHolder{
            ImageView icoView;
            TextView songView;
            TextView albumView;
        }
        public AlbumListAdapter(Context context){
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return MusicUtils.albumlList.size();
        }

        @Override
        public Object getItem(int position) {
            return MusicUtils.albumlList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view==null){
                view=View.inflate(AppliContext.sContext,R.layout.album_music_item,null);
                viewHolder=new ViewHolder();
                viewHolder.icoView=view.findViewById(R.id.iv_album_layout_ico);
                viewHolder.songView=view.findViewById(R.id.tv_album_list_songs);
                viewHolder.albumView=view.findViewById(R.id.tv_album_list_album);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) view.getTag();
            }
            Music music= (Music) getItem(position);
            Bitmap icon = BitmapFactory.decodeFile(music.getImage());
            viewHolder.icoView.setImageBitmap(icon == null ?
                    BitmapFactory.decodeResource(
                            AppliContext.sContext.getResources(), R.mipmap.img) : icon);
            viewHolder.albumView.setText(music.getAlbumName());
            String songs = String.format( AppliContext.sContext.getResources().getString( R.string.album_songs_string), music.getAlbumSongs());
            viewHolder.songView.setText(songs);
            return view;
        }
    }
}
