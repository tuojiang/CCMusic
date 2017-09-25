package oyh.ccmusic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oyh.ccmusic.R;

/**
 * 专辑列表
 * A simple {@link Fragment} subclass.
 */
public class AlbumMusicFragment extends Fragment {


    public AlbumMusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album_music, container, false);
    }

}
