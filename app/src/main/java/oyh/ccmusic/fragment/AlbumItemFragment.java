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
 * 点击专辑列表页面
 * A simple {@link Fragment} subclass.
 */
public class AlbumItemFragment extends Fragment {

    private MainActivity mActivity;
    private ListView listView;
    private ImageView icoUp;
    private TextView albumName,artist;
    private AlbumItemListAdapter adapter;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction transaction;
    public AlbumItemFragment() {
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
        View layout = inflater.inflate(R.layout.fragment_album_item, null);
        listView=layout.findViewById(R.id.lv_album_item_music);
        adapter=new AlbumItemListAdapter();
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
        icoUp=view.findViewById(R.id.iv_album_item_ico_up);
        albumName=view.findViewById(R.id.tv_album_item_albumname);
        artist=view.findViewById(R.id.tv_album_item_artist);

        icoUp.setImageBitmap(icoUp==null?BitmapFactory.decodeResource(AppliContext.sContext.getResources(),R.mipmap.img):ico);
        albumName.setText(musicIcoUp.getAlbumName());
        artist.setText(musicIcoUp.getArtist());

    }

    public class AlbumItemListAdapter extends BaseAdapter{

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
                view=View.inflate(AppliContext.sContext,R.layout.album_detail_item,null);
                viewHolder.ico=view.findViewById(R.id.iv_album_item_ico);
                viewHolder.title=view.findViewById(R.id.tv_album_item_list_title);
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
