package oyh.ccmusic.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

/**
 * 艺术家列表
 * A simple {@link Fragment} subclass.
 */
public class ArtistMusicFragment extends Fragment {

    private ListView mListView;
    private MainActivity mActivity;
    private ArtistListAdapter adapter;
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
        adapter=new ArtistListAdapter(mActivity);
        mListView=layout.findViewById(R.id.lv_artist_music);
        mListView.setAdapter(adapter);
        return layout;
    }
    public class ArtistListAdapter extends BaseAdapter{
        private LayoutInflater inflater;
        class ViewHolder{
            ImageView icoView;
            TextView artistView;
            TextView albumAndSongView;
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
                view=View.inflate(AppliContext.sContext,R.layout.artist_music_item,null);
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
