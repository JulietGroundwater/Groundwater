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
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.activities.MainActivity;
import uk.ac.cam.cl.juliet.adapters.FilesListAdapter;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/**
 * Fragment for the 'data' screen.
 *
 * @author Ben Cole
 */
public class DataFragment extends Fragment
        implements FilesListAdapter.OnDataFileSelectedListener,
                MainActivity.PermissionListener,
                View.OnClickListener {

    public static final String FOLDER_PATH = "folder_path";

    private RecyclerView filesRecyclerView;
    private TextView noFilesToDisplayText;
    private FilesListAdapter adapter;
    private SingleOrManyBursts currentNode;
    private List<SingleOrManyBursts> filesList;
    private Button plotAllFilesButton;

    DataFragmentListener listener;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        // Get the context
        Context context = getContext();
        if (context == null) return null;

        // Extract the folder path for this DataFragment to display
        Bundle arguments = getArguments();
        if (arguments == null
                || !arguments.containsKey(FOLDER_PATH)
                || !(arguments.get(FOLDER_PATH) instanceof String)) {
            return null; // TODO: handle this better
        }
        String folderPath = arguments.getString(FOLDER_PATH);
        File folder = new File(folderPath);
        currentNode = getDataFiles(folder);
        try {
            filesList = currentNode.getListOfBursts();
        } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
            e.printStackTrace();
        }

        // Set up the UI
        filesRecyclerView = view.findViewById(R.id.filesListRecyclerView);
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FilesListAdapter(filesList);
        adapter.setOnDataFileSelectedListener(this);
        filesRecyclerView.setAdapter(adapter);
        noFilesToDisplayText = view.findViewById(R.id.noFilesText);
        int visibility = filesList.isEmpty() ? View.VISIBLE : View.INVISIBLE;
        noFilesToDisplayText.setVisibility(visibility);

        // Set up and potentially disable the "plot all filesList" button
        plotAllFilesButton = view.findViewById(R.id.displayAllFilesButton);
        if (!getEligibleForPlottingAllFiles()) {
            plotAllFilesButton.setEnabled(false);
        }
        plotAllFilesButton.setOnClickListener(this);

        // Subscribe for permission updates
        MainActivity main = (MainActivity) getActivity();
        if (main != null) main.addListener(this);

        // Return the View that was created
        return view;
    }

    /**
     * Loads all files in the directory that was passed as an argument to the fragment.
     *
     * @return A <code>List</code> of all <code>SingleOrManyBursts</code> in this directory.
     */
    private SingleOrManyBursts loadFilesInFolder(File folder) {
        List<SingleOrManyBursts> files = new ArrayList<>();
        if (!folder.isFile()) {
            for (File file : folder.listFiles()) {
                SingleOrManyBursts inner;
                if (file.isFile()) {
                    inner =
                            new SingleOrManyBursts(
                                    (Burst) null,
                                    file,
                                    false); // TODO: Detect if synced to OneDrive
                } else {
                    inner =
                            new SingleOrManyBursts(
                                    (ArrayList<SingleOrManyBursts>) null, file, false);
                }
                files.add(inner);
            }
        } // TODO: Throw exception if attempt to load files from a file??
        return new SingleOrManyBursts(files, folder, false); // TODO: Detect if synced to OneDrive
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
     * Triggers all the files in a collection to be plotted, and subsequently the UI to swap from
     * displaying the list of files to the plot of the collection.
     */
    private void plotCollection() {
        InternalDataHandler idh = InternalDataHandler.getInstance();
        idh.setSelectedData(currentNode);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showChartScreen();
        }
    }

    /**
     * Returns a list of all data files that are stored on the device.
     *
     * @return an ArrayList of data files stored on the device
     */
    private SingleOrManyBursts getRootNode() {
        InternalDataHandler idh = InternalDataHandler.getInstance();
        List<SingleOrManyBursts> files = new ArrayList<>();

        File groundwater;

        if (ContextCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            groundwater = idh.getRoot();
            try {
                files = getDataFiles(groundwater).getListOfBursts();
            } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }

        // TODO: Check for OneDrive sync
        return new SingleOrManyBursts(files, groundwater, false);
    }

    /**
     * Recursively searches the file structure, starting at the passed file, finding all data
     * folders and files that can be displayed.
     *
     * @param folder The folder from which to start the search
     * @return A SingleOrManyBursts instance containing the tree of files
     */
    private SingleOrManyBursts getDataFiles(File folder) {
        SingleOrManyBursts result;
        if (folder.isFile()) {
            result =
                    new SingleOrManyBursts(
                            (Burst) null, folder, false); // TODO: Check for OneDrive sync
        } else {
            List<SingleOrManyBursts> values = new ArrayList<>();
            result = new SingleOrManyBursts(values, folder, false);
            for (File innerFile : folder.listFiles()) {
                SingleOrManyBursts singleOrManyBursts = getDataFiles(innerFile);
                values.add(singleOrManyBursts);
            }
            result.setFile(folder);
        }
        return result;
    }

    /**
     * Determines whether the folder we are current looking at is appropriate for plotting all
     * files.
     *
     * <p>This will be true if the current folder is both non-empty and contains only files (no
     * folders). Otherwise, this will be false.
     *
     * @return
     */
    private boolean getEligibleForPlottingAllFiles() {
        if (filesList.isEmpty()) return false;
        boolean eligible = true;
        for (SingleOrManyBursts file : filesList) {
            eligible &= file.getIsSingleBurst();
        }
        return eligible;
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
        // System.out.println("ESTOY AQUI");
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
            filesList.addAll(getRootNode().getListOfBursts());
        } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
            e.printStackTrace();
        }
        // Change visibility of the no filesList message
        int visibility = filesList.isEmpty() ? View.VISIBLE : View.INVISIBLE;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.displayAllFilesButton:
                plotCollection();
        }
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
