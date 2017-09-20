package oyh.ccmusic.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.activity.MainActivity;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

/**
 * Created by yihong.ou on 17-9-7.
 */
public class LocalMusicListAdapter extends BaseAdapter {
    private static final String TAG = "LocalMusicListAdapter";
    private int mPlayingPosition;
    public LocalMusicListAdapter() {
    }

    public void setPlayingPosition(int position) {
        mPlayingPosition = position;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return MusicUtils.sMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return MusicUtils.sMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "LocalMusicListAdapter count: " + getCount());
        ViewHolder viewHolder= null;
        MyListener myListener=null;
        if (convertView == null) {
            convertView = View.inflate(AppliContext.sContext, R.layout.local_music_item, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.music_list_icon);
            viewHolder.title = convertView.findViewById(R.id.tv_music_list_title);
            viewHolder.artist = convertView.findViewById(R.id.tv_music_list_artist);
            viewHolder.menuImage=convertView.findViewById(R.id.iv_aplist_btn);
            viewHolder.mark = convertView.findViewById(R.id.music_list_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
//        if (mPlayingPosition == position)
//            viewHolder.mark.setVisibility(View.VISIBLE);
//        else
//            viewHolder.mark.setVisibility(View.INVISIBLE);
        myListener=new MyListener(position);
        Music music = (Music) getItem(position);

        Bitmap icon = BitmapFactory.decodeFile(music.getImage());
        viewHolder.icon.setImageBitmap(icon == null ?
                BitmapFactory.decodeResource(
                        AppliContext.sContext.getResources(), R.mipmap.img) : icon);
        viewHolder.title.setText(music.getTitle());
        viewHolder.artist.setText(music.getArtist());
        viewHolder.menuImage.setOnClickListener(myListener);
        return convertView;
    }
    private class MyListener implements View.OnClickListener {
        int mPosition;
        Music music;
        public MyListener(int inPosition){
            mPosition= inPosition;
            music= (Music) getItem(inPosition);
        }
        @Override
        public void onClick(View v) {
            String title=music.getTitle();
            Toast.makeText(AppliContext.sContext,"已添加 [ "+title+" ] 到我喜欢列表",Toast.LENGTH_SHORT).show();
//            mMainActivity.getLocalMusicService().addplaylist(music);
            //TODO 待处理点击事件
        }

    }
    class ViewHolder {
        ImageView icon;
        TextView title, artist;
        ImageView menuImage;
        View mark;
    }
}
