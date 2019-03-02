package uk.ac.cam.cl.juliet.data;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** A Class that handles all of the internal data passing and manipulation */
public class InternalDataHandler {

    private static InternalDataHandler INSTANCE;
    private static final String ROOT_NAME = "groundwater";
    private File root;
    private SingleOrManyBursts singleSelected;
    private SingleOrManyBursts collectionSelected;
    private List<FileListener> singleListeners;
    private List<FileListener> collectionListeners;
    private boolean rootEmpty;
    private String currentLiveData;
    private boolean processingLiveData = false;

    public static InternalDataHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InternalDataHandler();
        }
        return INSTANCE;
    }

    private InternalDataHandler() {
        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT_NAME);
        rootEmpty = (root.listFiles() == null);
        if (singleListeners == null) {
            singleListeners = new ArrayList<>();
        }
        if (collectionListeners == null) {
            collectionListeners = new ArrayList<>();
        }
    }

    /**
     * Set the globally single file selected and notify listeners
     *
     * @param selectedData is a <code>SingleOrManyBursts</code> instance
     */
    public void setSingleSelected(SingleOrManyBursts selectedData) {
        this.singleSelected = selectedData;
        for (FileListener listener : singleListeners) {
            listener.onChange();
        }
    }

    /**
     * Set the globally single file selected and notify listeners
     *
     * @param selectedData is a <code>SingleOrManyBursts</code> instance
     */
    public void setCollectionSelected(SingleOrManyBursts selectedData) {
        this.collectionSelected = selectedData;
        for (FileListener listener : collectionListeners) {
            listener.onChange();
        }
    }

    /**
     * Set the globally selected data and don't notify listeners
     *
     * @param selectedData
     */
    public void silentlySelectCollectionData(SingleOrManyBursts selectedData) {
        this.collectionSelected = selectedData;
    }

    /**
     * For adding new single file listeners to the selected file changes
     *
     * @param listener
     */
    public void addSingleListener(FileListener listener) {
        this.singleListeners.add(listener);
    }

    /**
     * For adding new single file listeners to the selected file changes
     *
     * @param listener
     */
    public void addCollectionListener(FileListener listener) {
        this.collectionListeners.add(listener);
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
     * Create a new directory with a given name on the external storage
     *
     * @param dirName - name of the directory
     */
    public File addNewDirectory(String dirName) {
        File file = new File(root.getAbsolutePath(), dirName);
        file.mkdir();
        return file;
    }

    /**
     * Creates a file in a given directory
     *
     * @param dirName - where to place the file
     * @param file - the file
     */
    public void addFileToDirectory(String dirName, File file) {
        File newFile = new File(root.getAbsolutePath() + "/" + dirName, file.getName());
        try (FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            fos.write(buffer);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // REMOVE THIS MEHTOD
    public void addFileToDirectory(String dirName, byte[] file) {
        File newFile = new File(root.getAbsolutePath() + "/" + dirName, "tests.json");
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            fos.write(file);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public File getSingleSelectedDataFile() {
        return getSingleSelected().getFile();
    }

    public File getCollectionSelectedDataFile() {
        return getCollectionSelected().getFile();
    }

    public File getFileByNameIn(String dirName, String filename) {
        return new File(root.getAbsolutePath() + "/" + dirName, filename);
    }

    public String getExternalStorageState() {
        return Environment.getExternalStorageState();
    }

    public SingleOrManyBursts getSingleSelected() {
        return this.singleSelected;
    }

    public SingleOrManyBursts getCollectionSelected() {
        return this.collectionSelected;
    }

    public void setRootEmpty(boolean emptyValue) {
        rootEmpty = emptyValue;
    }

    public void setCurrentLiveData(String name) {
        this.currentLiveData = name;
    }

    public String getCurrentLiveData() {
        return this.currentLiveData;
    }

    public boolean isRootEmpty() {
        return rootEmpty;
    }

    public void setProcessingLiveData(boolean value) {
        processingLiveData = value;
    }

    public boolean getProcessingLiveData() {
        return processingLiveData;
    }

    public List<FileListener> getSingleListeners() {
        return singleListeners;
    }

    public List<FileListener> getCollectionListeners() {
        return collectionListeners;
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
