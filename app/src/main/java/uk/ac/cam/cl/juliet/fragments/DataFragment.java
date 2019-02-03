package uk.ac.cam.cl.juliet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.identity.client.*;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.adapters.FilesListAdapter;
import uk.ac.cam.cl.juliet.data.AuthenticationManager;
import uk.ac.cam.cl.juliet.data.Connect;
import uk.ac.cam.cl.juliet.data.GraphServiceController;
import uk.ac.cam.cl.juliet.data.IAuthenticationCallback;

/**
 * Fragment for the 'data' screen.
 *
 * @author Ben Cole
 */
public class DataFragment extends Fragment implements FilesListAdapter.OnDataFileSelectedListener, IAuthenticationCallback {

    private RecyclerView filesList;
    private FilesListAdapter adapter;
    private MenuItem signIn;
    private MenuItem signOut;
    private User user;

    /**
     * A temporary type to be used so I can test the UI for displaying files before the actual file
     * handling code is written. This class should eventually be deleted!
     */
    public static class TemporaryDataFileType {
        public String timestamp;
        public String gps;
        public boolean syncStatus;

        public TemporaryDataFileType(String timestamp, String gps, boolean syncStatus) {
            this.timestamp = timestamp;
            this.gps = gps;
            this.syncStatus = syncStatus;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Connect.getInstance().setConnectActivityInstanceInstance(getActivity());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        if (context == null) return null;
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        filesList = view.findViewById(R.id.filesListRecyclerView);
        filesList.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider =
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        filesList.addItemDecoration(divider);
        ArrayList<TemporaryDataFileType> files = getDataFiles();
        adapter = new FilesListAdapter(files);
        adapter.setOnDataFileSelectedListener(this);
        filesList.setAdapter(adapter);
        // Init the menu items
        signIn = view.findViewById(R.id.sign_in_button);
        signOut = view.findViewById(R.id.sign_out_button);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity != null) activity.setTitle(R.string.title_data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_button:
                showSyncDialog();
                return true;
            case R.id.sign_in_button:
                // TODO: Show a sign in dialog
                // Handling Microsoft connection
                connect();
                Toast.makeText(getContext(), "Handling sign in...", Toast.LENGTH_LONG).show();
                return true;
            case R.id.sign_out_button:
                // Disconnect
                AuthenticationManager.getInstance().disconnect();
                signOut.setVisible(false);
                signIn.setVisible(true);
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Displays a dialog when a file is selected.
     *
     * @param file The file from the list that the user selected.
     */
    @Override
    public void onDataFileClicked(
            final TemporaryDataFileType file,
            final FilesListAdapter.FilesListViewHolder viewHolder) {
        Context context = getContext();
        if (context == null) return;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_file_selected);
        dialog.findViewById(R.id.deleteButton)
                .setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
        dialog.findViewById(R.id.syncButton)
                .setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO: sync the file to the server
                                uploadFile(file, viewHolder);
                                dialog.cancel();
                            }
                        });
        dialog.findViewById(R.id.displayButton)
                .setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO: display the file's data
                                dialog.cancel();
                            }
                        });
        dialog.show();
    }

    /**
     * Returns a list of all Data Files that are stored on the device.
     *
     * @return an ArrayList of data files stored on the device
     */
    private ArrayList<TemporaryDataFileType> getDataFiles() {
        // TODO: Actually load data files!
        ArrayList<TemporaryDataFileType> files = new ArrayList<>();
        files.add(new TemporaryDataFileType("31/1/2019", "GPS location here", false));
        files.add(new TemporaryDataFileType("30/1/2019", "GPS location here", true));
        files.add(new TemporaryDataFileType("29/1/2019", "GPS location here", true));
        return files;
    }

    /** Displays a dialog for syncing the files with the server. */
    private void showSyncDialog() {
        Context context = getContext();
        if (context == null) return;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_upload_files);
        final CheckBox deleteAfterUploadingCheckbox =
                dialog.findViewById(R.id.deleteAfterUploadingCheckbox);
        dialog.findViewById(R.id.uploadButton)
                .setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadAllUnsyncedFiles(deleteAfterUploadingCheckbox.isChecked());
                                dialog.cancel();
                            }
                        });
        dialog.findViewById(R.id.cancelButton)
                .setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
        dialog.show();
    }

    /**
     * Reloads and redraws the list of files.
     *
     * <p>Call when the set of files has been modified.
     */
    private void notifyFilesChanged() {
        adapter.notifyDataSetChanged();
    }

    /**
     * Handles spawning a background thread and uploading a file to OneDrive.
     *
     * <p>As well as uploading the file, this method also handles showing and subsequently hiding
     * the progress spinner for the relevant RecyclerView row.
     *
     * @param file The file to upload
     * @param viewHolder The ViewHolder of the row that was selected
     */
    private void uploadFile(
            TemporaryDataFileType file, FilesListAdapter.FilesListViewHolder viewHolder) {
        new UploadFileTask(this, viewHolder).execute(file);
    }

    /**
     * Uploads all unsynced files to OneDrive.
     *
     * @param deleteAfterUploading true if files should be deleted after uploading; false to keep
     *     files on device after uploading
     */
    private void uploadAllUnsyncedFiles(boolean deleteAfterUploading) {
        // TODO: implement
    }

    /**
     * Begins the authentication process with Microsoft
     */
    private void connect() {
        // Get the Authentication Manager Instance
        AuthenticationManager authManager = AuthenticationManager.getInstance();

        // Get the public client application
        PublicClientApplication clientApp = authManager.getPublicClient();

        // Try and access the users
        List<User> users = null;

        try {
            users = clientApp.getUsers();
            if (users != null && users.size() == 1) {
                // There is a cached user so silently login
                authManager.acquireTokenSilently(users.get(0), true, this);
            } else {
                // There are no cached users so interactively login
                authManager.acquireToken(getActivity(), this);
            }
        } catch (MsalClientException e) {
            e.printStackTrace();
        }
    }

    private void configureMenu() {
        try {
            List<User> users = AuthenticationManager.getInstance().getPublicClient().getUsers();
            if (users.size() != 1) {
                signIn.setVisible(true);
                signOut.setVisible(false);
            }
        } catch (MsalClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * On successful authentication set the user
     *
     * @param res the authetnication result
     */
    @Override
    public void onSuccess(AuthenticationResult res) {
        user = res.getUser();
        // Swap visibility of the buttons
        signIn.setVisible(false);
        signOut.setVisible(true);
    }

    /**
     * Notify if there is an error
     *
     * @param msalException
     */
    @Override
    public void onError(MsalException msalException) {
        Toast.makeText(getContext(), "An error occurred whilst logging you in", Toast.LENGTH_LONG).show();
    }

    /**
     * Notify if the user cancels
     */
    @Override
    public void onCancel() {
        Toast.makeText(getContext(), "The user cancelled logging in", Toast.LENGTH_LONG).show();
    }

    /** Asynchronously uploads a file to OneDrive. */
    private static class UploadFileTask extends AsyncTask<TemporaryDataFileType, Void, Boolean> {

        private TemporaryDataFileType file;
        private DataFragment parent;
        private FilesListAdapter.FilesListViewHolder viewHolder;
        private GraphServiceController gsc;

        public UploadFileTask(
                DataFragment parent, FilesListAdapter.FilesListViewHolder viewHolder) {
            super();
            this.parent = parent;
            this.viewHolder = viewHolder;
            this.gsc = new GraphServiceController();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewHolder.setSpinnerVisibility(true);
            viewHolder.setSyncStatusVisibility(false);
        }

        @Override
        protected Boolean doInBackground(TemporaryDataFileType... temporaryDataFileTypes) {
            if (temporaryDataFileTypes.length < 1) return false;
            try {
                file = temporaryDataFileTypes[0];
                // TODO: Send it to the server!
                // Send the data using the graph service controller
                // gsc.uploadDatafile(file.timestamp + "@" + file.gps, "dat", file.data.getBytes(), callback);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            file.syncStatus = success;
            viewHolder.setSpinnerVisibility(false);
            viewHolder.setSyncStatusVisibility(true);
            parent.notifyFilesChanged();
        }
    }
}
