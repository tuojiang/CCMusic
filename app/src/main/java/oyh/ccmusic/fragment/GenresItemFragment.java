package oyh.ccmusic.fragment;


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
 * A simple {@link Fragment} subclass.
 */
public class GenresItemFragment extends Fragment {
    private MainActivity mActivity;
    private ListView listView;
    private ImageView icoUp;
    private TextView genrestSongs,genresName;
    private GenresItemListAdapter adapter;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;

    public GenresItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_genres_item, container, false);
        listView=layout.findViewById(R.id.lv_genres_item_detail);
        adapter=new GenresItemListAdapter();
        listView.setAdapter(adapter);
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
        icoUp=view.findViewById(R.id.iv_genres_item_ico_up);
        genresName=view.findViewById(R.id.tv_genres_item_name);
        genrestSongs=view.findViewById(R.id.tv_genres_item_songs);
        String amount= MusicUtils.map.get(musicIcoUp.getGenres());
        icoUp.setImageBitmap(icoUp==null?BitmapFactory.decodeResource(AppliContext.sContext.getResources(),R.mipmap.img):ico);
        String songs =String.format(AppliContext.sContext.getResources().getString(R.string.genres_songs_string),amount);
        genrestSongs.setText(songs);
        genresName.setText(musicIcoUp.getGenres());

    }
    public class GenresItemListAdapter extends BaseAdapter{

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
                view=View.inflate(AppliContext.sContext,R.layout.genres_detail_item,null);
                viewHolder.ico=view.findViewById(R.id.iv_genres_item_ico);
                viewHolder.title=view.findViewById(R.id.tv_genres_item_list_title);
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
