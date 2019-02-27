package uk.ac.cam.cl.juliet.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/**
 * Contains the active <code>DataFragment</code> and handles all fragment transactions required for
 * navigating the file structure tree.
 */
public class DataFragmentWrapper extends Fragment
        implements DataFragment.OnInnerFolderClickedListener {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_wrapper, container, false);
        DataFragment dataFragment = new DataFragment();
        dataFragment.setArguments(getArguments());
        dataFragment.setOnInnerFolderClickedListener(this);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction().add(R.id.dataFragmentContent, dataFragment).commit();
        }
        return view;
    }

    @Override
    public void onInnerFolderClicked(SingleOrManyBursts innerFolder) {
        if (innerFolder.getIsSingleBurst()) return; // Should not happen...

        DataFragment innerFragment = new DataFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(DataFragment.TOP_LEVEL, false);
        arguments.putSerializable(DataFragment.FILES_LIST, innerFolder);
        innerFragment.setArguments(arguments);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.dataFragmentContent, innerFragment, innerFragment.getTag())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
