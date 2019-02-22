package uk.ac.cam.cl.juliet.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.extensions.DriveItem;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalClientException;
import com.microsoft.identity.client.MsalException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.adapters.FilesListAdapter;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
import uk.ac.cam.cl.juliet.data.AuthenticationManager;
import uk.ac.cam.cl.juliet.data.GraphServiceController;
import uk.ac.cam.cl.juliet.data.IAuthenticationCallback;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/**
 * Fragment for the 'data' screen.
 *
 * @author Ben Cole
 */
public class DataFragment extends Fragment
        implements FilesListAdapter.OnDataFileSelectedListener, IAuthenticationCallback {

    public static String TOP_LEVEL = "top_level";
    public static String FILES_LIST = "files_list";

    private RecyclerView filesList;
    private TextView noFilesToDisplayText;
    private FilesListAdapter adapter;
    private MenuItem signIn;
    private MenuItem signOut;
    private User user;
    private List<SingleOrManyBursts> files;

    /**
     * If this is the fragment displaying the top level then it will load its files globally.
     * Otherwise, this fragment will display a list of files passed to it.
     */
    private boolean isTopLevel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        setHasOptionsMenu(true);

        // Get the context
        Context context = getContext();
        if (context == null) return null;

        // Determine whether this is a top level or a nested Fragment, which determines where
        // the data files should be loaded from (global source for top level, passed as argument
        // for nested)
        isTopLevel = getIsTopLevel();
        if (isTopLevel) {
            try {
                files = getDataFiles();
            } catch (InvalidBurstException e) {
                e.printStackTrace();
                // TODO: display error message
                return null;
            }
        } else {
            files = loadPassedFiles();
        }

        // Set up the UI
        filesList = view.findViewById(R.id.filesListRecyclerView);
        filesList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FilesListAdapter(files);
        adapter.setOnDataFileSelectedListener(this);
        filesList.setAdapter(adapter);
        noFilesToDisplayText = view.findViewById(R.id.noFilesText);
        int visibility = files.isEmpty() ? View.VISIBLE : View.INVISIBLE;
        noFilesToDisplayText.setVisibility(visibility);

        // Return the View that was created
        return view;
    }

    /**
     * Determines whether this fragment is the top level in the file hierarchy.
     *
     * @return true if this is the top level; false otherwise
     */
    private boolean getIsTopLevel() {
        Bundle arguments = getArguments();
        if (arguments == null) return true;
        return arguments.getBoolean(TOP_LEVEL, true);
    }

    /**
     * Returns the list of files passed to this Fragment.
     *
     * @return The list of files passed to this Fragment.
     */
    private List<SingleOrManyBursts> loadPassedFiles() {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(FILES_LIST)) {
            Object selectedFile = arguments.get(FILES_LIST);
            if (selectedFile instanceof SingleOrManyBursts) {
                SingleOrManyBursts singleOrManyBursts = (SingleOrManyBursts) selectedFile;
                try {
                    return singleOrManyBursts.getListOfBursts();
                } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_button:
                showSyncDialog();
                return true;
            case R.id.sign_in_button:
                // Handling Microsoft connection
                connect();
                return true;
            case R.id.sign_out_button:
                // Disconnect
                // TODO: Display some kind of "signed out" message
                try {
                    AuthenticationManager.getInstance().disconnect();
                } catch (MsalClientException msal) {
                    msal.printStackTrace();
                }
                signOut.setVisible(false);
                signIn.setVisible(true);
        }
        return false;
    }

    /**
     * A method that is called on tab selection - checking for a user still logged in
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // Handle viewing the correct menu buttons
        displayCorrectAuthButtons();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_sync, menu);
        // Init the menu items
        signIn = menu.getItem(0);
        signOut = menu.getItem(1);
        displayCorrectAuthButtons();
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Displays a dialog when a file is selected.
     *
     * @param file The file from the list that the user selected.
     */
    @Override
    public void onDataFileClicked(
            final SingleOrManyBursts file, final FilesListAdapter.FilesListViewHolder viewHolder) {
        Context context = getContext();
        if (context == null) return;
        if (file.getIsSingleBurst()) {
            Toast.makeText(context, "Display the file.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Display folder contents.", Toast.LENGTH_SHORT).show();
            displayNestedFolder(file);
        }
        // Set the selected data to the correct file
        InternalDataHandler idh = InternalDataHandler.getInstance();
        idh.setSelectedData(file);
    }

    private void displayNestedFolder(SingleOrManyBursts file) {
        // TODO: Integrate with other code and just display this file instead if this happens
        if (file.getIsSingleBurst()) return; // Should not happen...

        DataFragment innerFragment = new DataFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(TOP_LEVEL, false);
        arguments.putSerializable(FILES_LIST, file);
        innerFragment.setArguments(arguments);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.dataFragmentContainer, innerFragment)
                .addToBackStack("Test") // TODO: Make sure this is unique!
                .commit();
    }

    @Override
    public boolean onDataFileLongClicked(
            final SingleOrManyBursts file, final FilesListAdapter.FilesListViewHolder viewHolder) {
        Context context = getContext();
        if (context == null) return false;
        int titleRes =
                (file.getIsSingleBurst()) ? R.string.file_selected : R.string.folder_selected;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleRes)
                .setMessage(R.string.what_do_with_file)
                .setPositiveButton(
                        R.string.sync,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                uploadFile(file, viewHolder);
                            }
                        })
                .setNeutralButton(
                        R.string.delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                showConfirmDeleteDialog(file);
                            }
                        })
                .setNegativeButton(
                        R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return true;
    }

    /**
     * Returns a list of all Data Files that are stored on the device.
     *
     * @return an ArrayList of data files stored on the device
     */
    private ArrayList<SingleOrManyBursts> getDataFiles() throws InvalidBurstException {
        InternalDataHandler idh = InternalDataHandler.getInstance();

        // Hardcoded groundwater SDCard Directory
        File[] groundwater = idh.getRoot().listFiles();
        ArrayList<SingleOrManyBursts> files = new ArrayList<>();

        // Iterate over files in the directory
        if (ContextCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            for (File file : groundwater) {
                // If it is a file then it is a single burst
                if (file.isFile()) {
                    // TODO: Check one drive sync
                    Burst burst = new Burst(file, 1);
                    files.add(new SingleOrManyBursts(burst, false));
                } else {
                    List<SingleOrManyBursts> list = new ArrayList<>();
                    // Otherwise it is a collection
                    for (File innerFile : file.listFiles()) {
                        list.add(new SingleOrManyBursts(new Burst(innerFile, 1), false));
                    }
                    SingleOrManyBursts many = new SingleOrManyBursts(list, false, file.getName());
                    files.add(many);
                }
            }
        }
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
     * A method for checking the current authentication status and setting the correct sign in or
     * out buttons
     */
    private void displayCorrectAuthButtons() {
        if (getView() == null || signIn == null || signOut == null) return;
        try {
            if (AuthenticationManager.getInstance().getPublicClient().getUsers().size() == 0) {
                signIn.setVisible(true);
                signOut.setVisible(false);
            } else {
                signIn.setVisible(false);
                signOut.setVisible(true);
            }
        } catch (MsalClientException msal) {
            msal.printStackTrace();
        }
    }

    /** Shows a dialog message to confirm whether a file or folder should be deleted. */
    private void showConfirmDeleteDialog(SingleOrManyBursts file) {
        Context context = getContext();
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_delete);
        builder.setMessage(R.string.are_you_sure_delete);
        builder.setPositiveButton(
                R.string.delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: delete the file
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
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
            SingleOrManyBursts file, FilesListAdapter.FilesListViewHolder viewHolder) {
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

    /** Begins the authentication process with Microsoft */
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
        Toast.makeText(getContext(), "An error occurred whilst logging you in", Toast.LENGTH_LONG)
                .show();
    }

    /** Notify if the user cancels */
    @Override
    public void onCancel() {
        Toast.makeText(getContext(), "The user cancelled logging in", Toast.LENGTH_LONG).show();
    }

    /** Asynchronously uploads a file to OneDrive. */
    private static class UploadFileTask extends AsyncTask<SingleOrManyBursts, Void, Boolean> {

        private SingleOrManyBursts file;
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
        protected Boolean doInBackground(SingleOrManyBursts... files) {
            if (files.length < 1) return false;
            try {
                file = files[0];
                // TODO: Send it to the server!
                // Send the data using the graph service controller
                AuthenticationManager auth = AuthenticationManager.getInstance();
                InternalDataHandler idh = InternalDataHandler.getInstance();
                if (auth.isUserLoggedIn()) {
                    File datafile = idh.getFileByName(file.getNameToDisplay());
                    gsc.uploadDatafile(
                            file.getNameToDisplay(),
                            "dat",
                            idh.convertToBytes(datafile),
                            new ICallback<DriveItem>() {
                                @Override
                                public void success(DriveItem driveItem) {
                                    Log.d("UPLOAD", "Upload was successful!");
                                }

                                @Override
                                public void failure(ClientException ex) {
                                    ex.printStackTrace();
                                }
                            });
                }
            } catch (MsalClientException msal) {
                msal.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException io) {
                io.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            file.setSyncStatus(success);
            viewHolder.setSpinnerVisibility(false);
            viewHolder.setSyncStatusVisibility(true);
            parent.notifyFilesChanged();
        }
    }
}
