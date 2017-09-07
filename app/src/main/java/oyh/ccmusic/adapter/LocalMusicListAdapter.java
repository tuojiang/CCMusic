package oyh.ccmusic.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.domain.Music;
import oyh.ccmusic.util.MusicUtils;

/**
 * Created by yihong.ou on 17-9-7.
 */
public class LocalMusicListAdapter extends BaseAdapter {
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
        ViewHolder viewHolder= null;
        if (convertView == null) {
            convertView = View.inflate(AppliContext.sContext, R.layout.local_music_item, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.music_list_icon);
            viewHolder.title = convertView.findViewById(R.id.tv_music_list_title);
            viewHolder.artist = convertView.findViewById(R.id.tv_music_list_artist);
            viewHolder.mark = convertView.findViewById(R.id.music_list_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if (mPlayingPosition == position)
            viewHolder.mark.setVisibility(View.VISIBLE);
        else
            viewHolder.mark.setVisibility(View.INVISIBLE);

        Music music = (Music) getItem(position);

        Bitmap icon = BitmapFactory.decodeFile(music.getImage());
        viewHolder.icon.setImageBitmap(icon == null ?
                BitmapFactory.decodeResource(
                        AppliContext.sContext.getResources(), R.mipmap.img) : icon);
        viewHolder.title.setText(music.getTitle());
        viewHolder.artist.setText(music.getArtist());

        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView title, artist;
        View mark;
    }
}
