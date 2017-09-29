package oyh.ccmusic.fragment;


import android.annotation.SuppressLint;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistItemFragment extends Fragment implements View.OnClickListener{
    private MainActivity mActivity;
    private ListView listView;
    private ImageView icoUp;
    private ImageView iv_back;
    private TextView artistSongs,artist;
    private ArtistItemListAdapter adapter;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;

    public ArtistItemFragment() {
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
        View layout = inflater.inflate(R.layout.fragment_artist_item, container, false);
        listView=layout.findViewById(R.id.lv_artist_item_detail);
        adapter=new ArtistItemListAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(mArtistItemClickListener);
        initView(layout);
        return layout;
    }
    /**
     * 初始化基本界面
     * @param view
     */
    public void initView(View view){
        Music musicIcoUp= (Music) MusicUtils.itemCommonList.get(0);
        Bitmap ico= BitmapFactory.decodeFile(musicIcoUp.getImage());
        icoUp=view.findViewById(R.id.iv_artist_item_ico_up);
        artist=view.findViewById(R.id.tv_artist_item_name);
        artistSongs=view.findViewById(R.id.tv_artist_item_songs);
        iv_back=view.findViewById(R.id.iv_back_artist);
        iv_back.setOnClickListener(this);

        icoUp.setImageBitmap(icoUp==null?BitmapFactory.decodeResource(AppliContext.sContext.getResources(),R.mipmap.img):ico);
        String songs =String.format(AppliContext.sContext.getResources().getString(R.string.artist_songs__string),musicIcoUp.getArtistSongs());
        artistSongs.setText(songs);
        artist.setText(musicIcoUp.getArtist());

    }

    /**
     * 点击监听
     */
    private AdapterView.OnItemClickListener mArtistItemClickListener=new AdapterView.OnItemClickListener() {
        @SuppressLint("LongLogTag")
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mActivity.Visiable();
            onPlay(i);
            fragmentManager=getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            ArtistPlayFragment artistPlayFragment=new ArtistPlayFragment();
            transaction.replace(R.id.music_detail_fragment,artistPlayFragment).addToBackStack(null).commit();
        }
    };

    private void onPlay(int position) {
        int pos=mActivity.getLocalMusicService().itemPlay(position);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back_artist:
                getActivity().onBackPressed();
                break;
        }
    }

    public class ArtistItemListAdapter extends BaseAdapter{
        class ViewHolder{
            ImageView ico;
            TextView title;
        }
        @Override
        public int getCount() {
            return MusicUtils.itemCommonList.size();
        }

        @Override
        public Object getItem(int i) {
            return MusicUtils.itemCommonList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view==null){
                viewHolder=new ViewHolder();
                view=View.inflate(AppliContext.sContext,R.layout.artist_detail_item,null);
                viewHolder.ico=view.findViewById(R.id.iv_artist_item_ico);
                viewHolder.title=view.findViewById(R.id.tv_artist_item_list_title);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) view.getTag();
            }
            Music music= (Music) getItem(i);
            viewHolder.title.setText(music.getTitle());
            Bitmap ico= BitmapFactory.decodeFile(music.getImage());
            viewHolder.ico.setImageBitmap(ico==null?BitmapFactory.decodeResource(AppliContext.sContext.getResources(),R.mipmap.img):ico);

            return view;
        }
    }
}
