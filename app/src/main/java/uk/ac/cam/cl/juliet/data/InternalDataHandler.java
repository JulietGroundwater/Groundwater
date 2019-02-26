package uk.ac.cam.cl.juliet.data;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** A Class that handles all of the internal data passing and manipulation */
public class InternalDataHandler {

    private static InternalDataHandler INSTANCE;
    private static final String ROOT_NAME = "groundwater";
    private File root;
    private SingleOrManyBursts selectedData;
    private List<FileListener> listeners;
    private boolean rootEmpty;

    public static InternalDataHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InternalDataHandler();
        }
        return INSTANCE;
    }

    public InternalDataHandler() {
        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT_NAME);
        rootEmpty = (root.listFiles() == null);
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
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
     * Simple helper function for converting file to byte array
     *
     * @param file
     * @return <code>byte[]</code>
     * @throws IOException
     */
    public byte[] convertToBytes(File file) throws IOException {
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray);
        fis.close();

        return bytesArray;
    }

    /**
     * Some helper methods we may need for getting files from the SD Card
     *
     * @param dirName
     * @return List of files in root + dirName (empty if root isn't there)
     */
    public List<String> getCollectionOfFiles(String dirName) {
        List<String> list = new ArrayList<>();
        if (!rootEmpty) {
            File dir = new File(root.getAbsolutePath(), dirName);
            for (File f : dir.listFiles()) {
                if (f.getName().contains("DAT")) {
                    list.add(f.getName());
                }
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

    public File getSelectedDataFile() {
        return getSelectedData().getFile();
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

    public void setRootEmpty(boolean emptyValue) {
        rootEmpty = emptyValue;
    }

    public boolean isRootEmpty() {
        return rootEmpty;
    }

    public List<FileListener> getListeners() {
        return listeners;
    }

    public interface FileListener {
        void onChange();
    }

    public class NoRootFoundException extends FileNotFoundException {
        public NoRootFoundException(String msg) {
            super(msg);
        }
    }
}
