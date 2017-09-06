package oyh.ccmusic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import oyh.ccmusic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalMusicFragment extends Fragment {


    public LocalMusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_local_music_list, container, false);
    }

}
