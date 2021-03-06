package oyh.ccmusic.adapter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import oyh.ccmusic.R;
import oyh.ccmusic.activity.AppliContext;
import oyh.ccmusic.domain.SearchResult;
import oyh.ccmusic.util.ImageUtils;
import oyh.ccmusic.util.MusicMemoryCacheUtils;

/**
 * Created by yihong.ou on 17-9-18.
 */
public class SearchResultAdapter extends BaseAdapter{
    private ArrayList<SearchResult> mSearchResult;
    public SearchResultAdapter(ArrayList<SearchResult> mResultData) {
        mSearchResult=mResultData;
    }

    @Override
    public int getCount() {
        return mSearchResult.size();
    }

    @Override
    public Object getItem(int i) {
        return mSearchResult.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view==null){
            view=View.inflate(AppliContext.sContext, R.layout.search_result_item,null);
            holder=new ViewHolder();
            holder.ico=view.findViewById(R.id.im_search_result_icon);
            holder.title=view.findViewById(R.id.tv_search_result_title);
            holder.artist=view.findViewById(R.id.tv_search_result_artist);
            holder.album=view.findViewById(R.id.tv_search_result_album);
            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }
        String artist = mSearchResult.get(i).getArtist();
        String album = mSearchResult.get(i).getAlbum();

        //TODO 获取图片

        holder.title.setText(mSearchResult.get(i).getMusicName());
        Bitmap icon= MusicMemoryCacheUtils.getInstance().load(mSearchResult.get(i).getImage());
        holder.ico.setImageBitmap(icon==null? ImageUtils
                .scaleBitmap(R.mipmap.img) : ImageUtils.scaleBitmap(icon));

        if(!TextUtils.isEmpty(artist)) {
            holder.artist.setText(artist);
        } else {
            holder.artist.setText("未知艺术家");
        }

        if(!TextUtils.isEmpty(album)) {
            holder.album.setText(album);
        } else {
            holder.album.setText("未知专辑");
        }
        return view;
    }
    static class ViewHolder{
        public ImageView ico;
        public TextView title;
        public TextView artist;
        public TextView album;
    }
}
