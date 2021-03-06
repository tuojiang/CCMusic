package oyh.ccmusic.adapter;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.ImageUtils;
import oyh.ccmusic.util.MusicMemoryCacheUtils;
import oyh.ccmusic.util.MusicUtils;

/**
 * Created by yihong.ou on 17-9-21.
 */
@TargetApi(Build.VERSION_CODES.N)
public class MyloveMusicListAdapter extends BaseAdapter {
    private static final String TAG = "MyloveMusicListAdapter";
    private static SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private ArrayList<Music> mMyLoveList;
    private boolean isGrid=false;
    public MyloveMusicListAdapter(ArrayList<Music> list) {
            mMyLoveList=list;
    }
    public MyloveMusicListAdapter(boolean isView) {
        isGrid=isView;
    }
    @Override
    public int getCount() {
        return mMyLoveList==null? MusicUtils.sMusicSQlList.size():mMyLoveList.size();
    }

    @Override
    public Object getItem(int i) {
        return mMyLoveList==null?MusicUtils.sMusicSQlList.get(i):mMyLoveList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if (view==null){
            if (isGrid==false) {
                view=View.inflate(AppliContext.sContext, R.layout.mylove_music_item,null);
            }else {
                view=View.inflate(AppliContext.sContext, R.layout.mylove_music_grid_item,null);
            }

            viewHolder=new ViewHolder();
            viewHolder.icon=view.findViewById(R.id.music_mllist_icon);
            viewHolder.title=view.findViewById(R.id.tv_music_mllist_title);
            viewHolder.artist=view.findViewById(R.id.tv_music_mllist_artist);
            viewHolder.duration=view.findViewById(R.id.tv_music_mllist_duration);
            viewHolder.mark=view.findViewById(R.id.music_mllist_selected);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        Music music= (Music) getItem(i);
        Bitmap icon = MusicMemoryCacheUtils.getInstance().load(music.getImage());
        viewHolder.icon.setImageBitmap(icon == null ?
                ImageUtils
                        .scaleBitmap(R.mipmap.img) : ImageUtils.scaleBitmap(icon));
        viewHolder.title.setText(music.getTitle());
        viewHolder.artist.setText(music.getArtist());
        String total = format.format(new Date(music.getLength()));
        viewHolder.duration.setText(total);
        return view;
    }
    class ViewHolder {
        ImageView icon;
        TextView title, artist,duration;
        View mark;
    }
}
