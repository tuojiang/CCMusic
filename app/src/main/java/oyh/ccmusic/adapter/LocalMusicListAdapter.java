package oyh.ccmusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.domain.Music;

/**
 * Created by yihong.ou on 17-9-7.
 */
public class LocalMusicListAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private ArrayList<Music> list = new ArrayList<>();
    private int mPlayingPosition;

    public LocalMusicListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public LocalMusicListAdapter() {
    }

    public void setPlayingPosition(int position) {
        mPlayingPosition = position;
        notifyDataSetChanged();
    }

    public void setListData(ArrayList<Music> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder= null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.local_music_item, parent, false);
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
