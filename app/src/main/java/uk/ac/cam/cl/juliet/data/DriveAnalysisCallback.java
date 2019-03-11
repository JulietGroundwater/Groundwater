package uk.ac.cam.cl.juliet.data;

import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.extensions.DriveItem;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import uk.ac.cam.cl.juliet.adapters.FilesListAdapter;
import uk.ac.cam.cl.juliet.fragments.DataFragment;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** Performs the call to check the sync status of the One Drive files */
public class DriveAnalysisCallback implements ICallback<DriveItem> {
    private File currentDirectory;
    private DataFragment fragment;
    private final List<SingleOrManyBursts> filesList;
    private FilesListAdapter adapterToNotify;

    public DriveAnalysisCallback(
            File currentDirectory,
            DataFragment fragment,
            List<SingleOrManyBursts> fileList,
            FilesListAdapter adapter) {
        this.currentDirectory = currentDirectory;
        this.fragment = fragment;
        this.filesList = fileList;
        this.adapterToNotify = adapter;
    }

    @Override
    public void success(DriveItem driveItem) {
        InternalDataHandler idh = InternalDataHandler.getInstance();
        // Iterate over the children of the current directory and add to synced file cache
        for (DriveItem child : driveItem.children.getCurrentPage()) {
            try {
                idh.addSyncedFile(
                        idh.getRelativeFromAbsolute(
                                (currentDirectory.getAbsolutePath()) + "/" + child.name));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        // Iterate over the files locally and check to see if they are in the synced cache
        for (SingleOrManyBursts single : filesList) {
            try {
                if (idh.getSyncedFiles()
                        .contains(
                                idh.getRelativeFromAbsolute(single.getFile().getAbsolutePath()))) {
                    single.setSyncStatus(true);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        adapterToNotify.notifyDataSetChanged();
        this.fragment.setCheckingSync(false);
    }

    @Override
    public void failure(ClientException ex) {
        System.out.println(
                "Could not find " + currentDirectory.getName() + " in the One Drive Folder");
    }
}
