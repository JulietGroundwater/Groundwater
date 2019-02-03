package uk.ac.cam.cl.juliet.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uk.ac.cam.cl.juliet.R;

/**
 * Fragment for the key information page.
 *
 * @author Ben Cole
 */
public class InfoOverviewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_overview, container, false);
    }
}
