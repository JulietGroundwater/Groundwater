package uk.ac.cam.cl.juliet.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.activities.MainActivity;
import uk.ac.cam.cl.juliet.adapters.FilesListAdapter;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/**
 * Fragment for the 'data' screen.
 *
 * @author Ben Cole
 */
public class DataFragment extends Fragment
        implements FilesListAdapter.OnDataFileSelectedListener, MainActivity.PermissionListener {

    public static String TOP_LEVEL = "top_level";
    public static String FILES_LIST = "files_list";

    private RecyclerView filesList;
    private TextView noFilesToDisplayText;
    private FilesListAdapter adapter;
    private List<SingleOrManyBursts> files;

    DataFragmentListener listener;

    /**
     * If this is the fragment displaying the top level then it will load its files globally.
     * Otherwise, this fragment will display a list of files passed to it.
     */
    private boolean isTopLevel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

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

        // Subscribe for permission updates
        MainActivity main = (MainActivity) getActivity();
        if (main != null) main.addListener(this);

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

            // Set the selected data to the correct file
            InternalDataHandler idh = InternalDataHandler.getInstance();
            idh.setSelectedData(file);

            // Show the plot of the data that the user just selected
            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).showChartScreen();
            }

        } else {
            displayNestedFolder(file);
        }
    }

    /**
     * Handles displaying the UI for an inner folder in place of this fragment.
     *
     * @param folder The folder to display
     */
    private void displayNestedFolder(SingleOrManyBursts folder) {
        if (listener != null) {
            listener.onInnerFolderClicked(folder);
        }
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
    private List<SingleOrManyBursts> getDataFiles() throws InvalidBurstException {
        // TODO: Redo this so that it returns a SingleOrManyBursts for the root, rather than
        // a list of files.

        InternalDataHandler idh = InternalDataHandler.getInstance();
        List<SingleOrManyBursts> files = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            File groundwater = idh.getRoot();
            try {
                files = getDataFiles(groundwater).getListOfBursts();
            } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
                e.printStackTrace();
            }
        }
        return files;
    }

    /**
     * Recursively searches the file structure, starting at the passed file, finding all data
     * folders and files that can be displayed.
     *
     * @param folder The folder from which to start the searc
     * @return A SingleOrManyBursts instance containing the tree of files
     */
    private SingleOrManyBursts getDataFiles(File folder) {
        SingleOrManyBursts result;
        if (folder.isFile()) {
            result = new SingleOrManyBursts((Burst) null, false, folder.getName(), null);
            result.setFile(folder);
        } else {
            List<SingleOrManyBursts> values = new ArrayList<>();
            result = new SingleOrManyBursts(values, false, folder.getName(), null);
            for (File innerFile : folder.listFiles()) {
                SingleOrManyBursts singleOrManyBursts = getDataFiles(innerFile);
                singleOrManyBursts.setParent(result);
                values.add(singleOrManyBursts);
            }
            result.setFile(folder);
        }
        return result;
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
    public void notifyFilesChanged() {
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
        if (listener == null) return;
        listener.uploadFile(this, viewHolder, file);
    }

    /** Called on permission granted - refresh file listing */
    @Override
    public void onPermissionGranted() {
        // Update the files now we have permission
        try {
            files.addAll(getDataFiles());
        } catch (InvalidBurstException e) {
            e.printStackTrace();
        }
        // Change visibility of the no files message
        int visibility = files.isEmpty() ? View.VISIBLE : View.INVISIBLE;
        noFilesToDisplayText.setVisibility(visibility);

        // Notify the adapter
        adapter.notifyDataSetChanged();
    }

    /**
     * Sets the listener for when a folder is clicked.
     *
     * @param listener The listener that will handle displaying the inner folder in place of this
     *     fragment
     */
    public void setDataFragmentListener(DataFragmentListener listener) {
        this.listener = listener;
    }

    /**
     * Used by a wrapper class so that this instance can be replaced with another instance to
     * display the contents of the folder that was selected.
     */
    public interface DataFragmentListener {
        void onInnerFolderClicked(SingleOrManyBursts innerFolder);

        void uploadFile(
                DataFragment parent,
                FilesListAdapter.FilesListViewHolder viewHolder,
                SingleOrManyBursts file);
    }
}
