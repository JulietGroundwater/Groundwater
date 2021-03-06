package uk.ac.cam.cl.juliet.fragments;

import static uk.ac.cam.cl.juliet.fragments.DataFragment.FOLDER_PATH;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.extensions.DriveItem;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalClientException;
import com.microsoft.identity.client.MsalException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.adapters.FilesListAdapter;
import uk.ac.cam.cl.juliet.data.AuthenticationManager;
import uk.ac.cam.cl.juliet.data.GraphServiceController;
import uk.ac.cam.cl.juliet.data.IAuthenticationCallback;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/**
 * Contains the active <code>DataFragment</code> and handles all fragment transactions required for
 * navigating the file structure tree.
 */
public class DataFragmentWrapper extends Fragment
        implements IAuthenticationCallback, DataFragment.DataFragmentListener {

    private MenuItem signIn;
    private MenuItem signOut;
    private MenuItem uploadAllFilesButton;
    private DataFragment currentFragment;
    private User user;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_wrapper, container, false);
        setHasOptionsMenu(true);
        Bundle arguments = new Bundle();
        arguments.putString(DataFragment.FOLDER_PATH, getRootPath());
        currentFragment = new DataFragment();
        currentFragment.setArguments(arguments);
        currentFragment.setDataFragmentListener(this);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.dataFragmentContent, currentFragment)
                    .commit();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_sync, menu);
        // Init the menu items
        signIn = menu.getItem(0);
        signOut = menu.getItem(1);
        uploadAllFilesButton = menu.findItem(R.id.sync_all_files_button);

        // Try silently logging in
        try {
            AuthenticationManager authManager = AuthenticationManager.getInstance();
            PublicClientApplication clientApp = authManager.getPublicClient();
            GraphServiceController gsc = new GraphServiceController();
            List<User> users = clientApp.getUsers();
            if (users != null && users.size() == 1) {
                // There is a cached user so silently login
                authManager.acquireTokenSilently(users.get(0), true, this);
                //                // Can now do the first check for files
                //                gsc.hasGroundwaterFolder(new RootCallback());
                //                currentFragment.refreshFiles();
            }
        } catch (MsalClientException ex) {
            ex.printStackTrace();
        }
        displayCorrectAuthButtons();
        updateUploadAllFilesButtonVisibility();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_all_files_button:
                showSyncDialog();
                return true;
            case R.id.refresh:
                // TODO: Update files in the DataFragment
                Log.d("DataFragmentWrapper", "Update your files!");
                currentFragment.refreshFiles();
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
                updateUploadAllFilesButtonVisibility();
                if (currentFragment != null) {
                    currentFragment.notifySignInStatusChanged(false);
                }
        }
        return false;
    }

    /**
     * Shows or hides the "upload all files" button based on whether the user is logged in.
     *
     * <p>If the user is signed in then the "upload all files" button will be shown; otherwise it
     * will be hidden.
     */
    private void updateUploadAllFilesButtonVisibility() {
        if (uploadAllFilesButton == null) return;
        boolean loggedIn = false;
        try {
            loggedIn = AuthenticationManager.getInstance().isUserLoggedIn();
        } catch (MsalClientException e) {
            e.printStackTrace();
            return;
        }
        uploadAllFilesButton.setEnabled(loggedIn);
        uploadAllFilesButton.setVisible(loggedIn);
    }

    private String getRootPath() {
        InternalDataHandler idh = InternalDataHandler.getInstance();
        Activity activity = getActivity();
        String path = null;
        if (activity != null
                && ContextCompat.checkSelfPermission(
                                activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
            path = idh.getRoot().getAbsolutePath();
        } else {
            // TODO: Show error message; we failed to get permissions
        }
        return path;
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
    public void onInnerFolderClicked(SingleOrManyBursts innerFolder) {
        if (innerFolder.getIsSingleBurst()) return; // Should not happen...

        currentFragment = new DataFragment();
        Bundle arguments = new Bundle();
        arguments.putString(FOLDER_PATH, innerFolder.getFile().getAbsolutePath());
        currentFragment.setArguments(arguments);
        currentFragment.setDataFragmentListener(this);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.dataFragmentContent, currentFragment, currentFragment.getTag())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void uploadFile(
            DataFragment parent,
            FilesListAdapter.FilesListViewHolder viewHolder,
            SingleOrManyBursts file,
            SingleOrManyBursts folder) {
        new UploadFileTask(parent, viewHolder, folder).execute(file);
    }

    /** Displays a dialog for syncing the files with the server. */
    private void showSyncDialog() {
        Context context = getContext();
        if (context == null) return;
        new AlertDialog.Builder(context)
                .setTitle(R.string.upload_unsynchronised_files)
                .setMessage(R.string.upload_unsyncronised_files_content)
                .setPositiveButton(
                        R.string.upload_all,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadAllUnsyncedFiles();
                                dialog.dismiss();
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
                .show();
    }

    /** Uploads all unsynced files to OneDrive. */
    private void uploadAllUnsyncedFiles() {
        try {
            currentFragment.uploadUnsyncedFiles();
        } catch (IOException io) {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(
                                context,
                                "There was something wrong with the file data!",
                                Toast.LENGTH_LONG)
                        .show();
            }
            io.printStackTrace();
        }
    }

    @Override
    public void notifyIsActiveFragment(DataFragment activeFragment) {
        currentFragment = activeFragment;
    }

    @Override
    public void notifyNoInternet() {
        displayCorrectAuthButtons();
        updateUploadAllFilesButtonVisibility();
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
     * @param res the authentication result
     */
    @Override
    public void onSuccess(AuthenticationResult res) {
        user = res.getUser();
        // Try and check the files
        currentFragment.refreshFiles();
        // Swap visibility of the buttons
        signIn.setVisible(false);
        signOut.setVisible(true);
        updateUploadAllFilesButtonVisibility();
        if (currentFragment != null) {
            currentFragment.notifySignInStatusChanged(true);
        }
    }

    /**
     * Notify if there is an error
     *
     * @param msalException
     */
    @Override
    public void onError(MsalException msalException) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, "An error occurred whilst logging you in", Toast.LENGTH_LONG)
                    .show();
        }
        updateUploadAllFilesButtonVisibility();
    }

    /** Notify if the user cancels */
    @Override
    public void onCancel() {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, "The user cancelled logging in", Toast.LENGTH_LONG).show();
        }
    }

    /** Asynchronously uploads a file to OneDrive. */
    private static class UploadFileTask extends AsyncTask<SingleOrManyBursts, Void, Boolean> {

        private SingleOrManyBursts file;
        private DataFragment parent;
        private FilesListAdapter.FilesListViewHolder viewHolder;
        private GraphServiceController gsc;
        private boolean success;
        private SingleOrManyBursts folder;

        public UploadFileTask(
                DataFragment parent,
                FilesListAdapter.FilesListViewHolder viewHolder,
                SingleOrManyBursts folder) {
            super();
            this.parent = parent;
            this.viewHolder = viewHolder;
            this.gsc = new GraphServiceController();
            this.success = false;
            this.folder = folder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewHolder.setSpinnerVisibility(true);
            viewHolder.setSyncStatusVisibility(false);
            Log.d("DataFragmentWrapper", "About to upload the file");
        }

        @Override
        protected Boolean doInBackground(SingleOrManyBursts... files) {
            if (files.length < 1) return false;
            try {
                file = files[0];
                // Send the data using the graph service controller
                final AuthenticationManager auth = AuthenticationManager.getInstance();
                final InternalDataHandler idh = InternalDataHandler.getInstance();
                if (auth.isUserLoggedIn()) {
                    gsc.uploadDatafile(
                            idh.getRelativeFromAbsolute(file.getFile().getAbsolutePath()),
                            idh.getRelativeFromAbsolute(folder.getFile().getAbsolutePath()),
                            idh.convertToBytes(file.getFile()),
                            new ICallback<DriveItem>() {
                                @Override
                                public void success(DriveItem driveItem) {
                                    Log.d("UPLOAD", "Upload was successful!");
                                    // Set the file in the global sync set
                                    try {
                                        idh.addSyncedFile(
                                                idh.getRelativeFromAbsolute(
                                                        file.getFile().getAbsolutePath()));
                                    } catch (FileNotFoundException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(ClientException ex) {
                                    System.out.println("FILE FAILURE");
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
