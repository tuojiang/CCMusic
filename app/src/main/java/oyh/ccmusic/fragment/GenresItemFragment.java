package oyh.ccmusic.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import oyh.ccmusic.util.ImageUtils;
import oyh.ccmusic.util.MusicMemoryCacheUtils;
import oyh.ccmusic.util.MusicUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenresItemFragment extends Fragment implements View.OnClickListener{
    private MainActivity mActivity;
    private ListView listView;
    private ImageView iv_back;
    private ImageView icoUp;
    private TextView genrestSongs,genresName;
    private GenresItemListAdapter adapter;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;

    public GenresItemFragment() {
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
        View layout = inflater.inflate(R.layout.fragment_genres_item, container, false);
        listView=layout.findViewById(R.id.lv_genres_item_detail);
        adapter=new GenresItemListAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(mGenresItemClickListener);
        initView(layout);
        return layout;

    }
    /**
     * 初始化基本界面
     * @param view
     */
    public void initView(View view){
        Music musicIcoUp= (Music) MusicUtils.itemCommonList.get(0);
        icoUp=view.findViewById(R.id.iv_genres_item_ico_up);
        genresName=view.findViewById(R.id.tv_genres_item_name);
        genrestSongs=view.findViewById(R.id.tv_genres_item_songs);
        iv_back=view.findViewById(R.id.iv_back_genres);
        iv_back.setOnClickListener(this);
        String amount= MusicUtils.map.get(musicIcoUp.getGenres());
//        Bitmap ico= BitmapFactory.decodeFile(musicIcoUp.getImage());
//        icoUp.setImageBitmap(icoUp==null?BitmapFactory.decodeResource(AppliContext.sContext.getResources(),R.mipmap.img):ico);
        Bitmap ico= MusicMemoryCacheUtils.getInstance().load(musicIcoUp.getImage());
        icoUp.setImageBitmap(icoUp==null? ImageUtils
                .scaleBitmap(R.mipmap.img) : ImageUtils.scaleBitmap(ico));
        String songs =String.format(AppliContext.sContext.getResources().getString(R.string.genres_songs_string),amount);
        genrestSongs.setText(songs);
        genresName.setText(musicIcoUp.getGenres());

    }

    /**
     * 点击监听
     */
    private AdapterView.OnItemClickListener mGenresItemClickListener=new AdapterView.OnItemClickListener() {
        @SuppressLint("LongLogTag")
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mActivity.Visiable();
            onPlay(i);
            fragmentManager=getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            GenresPlayFragment genresPlayFragment=new GenresPlayFragment();
            transaction.replace(R.id.music_detail_fragment,genresPlayFragment).addToBackStack(null).commit();
        }
    };

    private void onPlay(int position) {
        int pos=mActivity.getLocalMusicService().itemPlay(position);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back_genres:
                getActivity().onBackPressed();
                break;
        }
    }

    public class GenresItemListAdapter extends BaseAdapter{

        class ViewHolder{
            ImageView ico;
            TextView title,artist;
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
                view=View.inflate(AppliContext.sContext,R.layout.genres_detail_item,null);
                viewHolder.ico=view.findViewById(R.id.iv_genres_item_ico);
                viewHolder.title=view.findViewById(R.id.tv_genres_item_list_title);
                viewHolder.artist=view.findViewById(R.id.tv_genres_item_list_artist);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) view.getTag();
            }
            Music music= (Music) getItem(i);
            viewHolder.title.setText(music.getTitle());
//            Bitmap ico= BitmapFactory.decodeFile(music.getImage());
//            viewHolder.ico.setImageBitmap(ico==null?BitmapFactory.decodeResource(AppliContext.sContext.getResources(),R.mipmap.img):ico);
            Bitmap ico= MusicMemoryCacheUtils.getInstance().load(music.getImage());
            viewHolder.ico.setImageBitmap(ico==null? ImageUtils
                    .scaleBitmap(R.mipmap.img) : ImageUtils.scaleBitmap(ico));
            viewHolder.artist.setText(music.getArtist());
            return view;
        }
    }
}
