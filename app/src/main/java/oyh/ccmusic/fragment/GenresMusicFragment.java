package oyh.ccmusic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oyh.ccmusic.R;

/**
 * 流派列表
 * A simple {@link Fragment} subclass.
 */
public class GenresMusicFragment extends Fragment {


    public GenresMusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genres_music, container, false);
    }

}
