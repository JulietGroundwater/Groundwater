package uk.ac.cam.cl.juliet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uk.ac.cam.cl.juliet.R;

/**
 * Fragment for the 'settings' screen.
 *
 * @author Ben Cole
 */
public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_settings);
    }
}
