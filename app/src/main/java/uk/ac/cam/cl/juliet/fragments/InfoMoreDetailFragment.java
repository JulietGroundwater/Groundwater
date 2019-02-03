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
 * Displays more detail about the currently open data file.
 *
 * @author Ben Cole
 */
public class InfoMoreDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_detail, container, false);
    }
}
