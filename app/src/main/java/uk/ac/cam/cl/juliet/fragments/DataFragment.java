package uk.ac.cam.cl.juliet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import uk.ac.cam.cl.juliet.R;

/**
 * Fragment for the 'data' screen.
 *
 * @author Ben Cole
 */
public class DataFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_button:
                showSyncDialog();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /** Displays a dialog for syncing the files with the server. */
    private void showSyncDialog() {
        Context context = getContext();
        if (context == null) return;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_sync_with_server);
        dialog.setTitle("Sync with server"); // TODO: Extract string resource
        dialog.show();
    }
}
