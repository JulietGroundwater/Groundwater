package uk.ac.cam.cl.juliet.data;

import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** A Class that handles all of the internal data passing and manipulation */
public class InternalDataHandler {

    private static InternalDataHandler INSTANCE;
    private static final String ROOT_NAME = "groundwater";
    private static File root;
    private static SingleOrManyBursts selectedData;
    private static List<FileListener> listeners;

    public static InternalDataHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InternalDataHandler();
            root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT_NAME);
            if (listeners == null) {
                listeners = new ArrayList<>();
            }
        }
        return INSTANCE;
    }

    /**
     * Set the globally selected data and notify listeners
     *
     * @param selectedData is a <code>SingleOrManyBursts</code> instance
     */
    public void setSelectedData(SingleOrManyBursts selectedData) {
        this.selectedData = selectedData;
        for (FileListener listener : listeners) {
            listener.onChange();
        }
    }

    /**
     * For adding new listeners to the selected file changes
     *
     * @param listener
     */
    public void addListener(FileListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Some helper methods we may need for getting files from the SD Card
     *
     * @param dirName
     * @return
     */
    public List<String> getCollectionOfFiles(String dirName) {
        List<String> list = new ArrayList<>();
        File dir = new File(root.getAbsolutePath(), dirName);
        for (File f : dir.listFiles()) {
            if (f.getName().contains("DAT")) {
                list.add(f.getName());
            }
        }

        return list;
    }

    public File getRoot() {
        return root;
    }

    public File getFileByName(String filename) {
        return new File(root.getAbsolutePath(), filename);
    }

    public File getFileByNameIn(String dirName, String filename) {
        return new File(root.getAbsolutePath() + "/" + dirName, filename);
    }

    public String getExternalStorageState() {
        return Environment.getExternalStorageState();
    }

    public SingleOrManyBursts getSelectedData() {
        return selectedData;
    }

    public static List<FileListener> getListeners() {
        return listeners;
    }

    public interface FileListener {
        void onChange();
    }
}
