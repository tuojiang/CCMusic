package oyh.ccmusic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oyh.ccmusic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MloveMusicFragment extends Fragment {


    public MloveMusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mlove_music, container, false);
    }

}
