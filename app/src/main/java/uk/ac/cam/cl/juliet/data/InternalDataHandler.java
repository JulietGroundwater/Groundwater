package uk.ac.cam.cl.juliet.data;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** A Class that handles all of the internal data passing and manipulation */
public class InternalDataHandler {

    public static final String ROOT_NAME = "groundwater";
    private static InternalDataHandler INSTANCE;
    private File root;
    private SingleOrManyBursts singleSelected;
    private SingleOrManyBursts collectionSelected;
    private List<FileListener> singleListeners;
    private List<FileListener> collectionListeners;
    private boolean rootEmpty;
    private String currentLiveData;
    private boolean processingLiveData = false;
    private boolean processingData = false;
    private Set<String> syncedFiles;

    public static InternalDataHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InternalDataHandler();
        }
        return INSTANCE;
    }

    private InternalDataHandler() {
        root = getRootDirectory();
        rootEmpty = (root.listFiles() == null);

        // Create the listeners for single changes
        if (singleListeners == null) {
            singleListeners = new ArrayList<>();
        }

        // Create the listeners for collection changes
        if (collectionListeners == null) {
            collectionListeners = new ArrayList<>();
        }

        // Create the synced files cache
        if (syncedFiles == null) {
            syncedFiles = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        }
    }

    /**
     * Returns a handle to the root directory for this app.
     *
     * <p>If the folder does not exist then it will be created.
     *
     * @return The <code>File</code> that refers to the root directory for this app.
     */
    private File getRootDirectory() {
        File systemRoot = Environment.getExternalStorageDirectory();
        File appRoot = null;
        for (File f : systemRoot.listFiles()) {
            if (f.getName().equals(ROOT_NAME) && f.isDirectory()) {
                appRoot = f;
                break;
            }
        }
        if (appRoot == null) {
            appRoot = new File(systemRoot, ROOT_NAME);
            appRoot.mkdir();
        }
        return appRoot;
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

    /**
     * A method for taking the absolute path from the Android device and return the root-relative
     * path
     *
     * @param absolutePath
     * @return a path relative to the identified root name (e.g. groundwater)
     * @throws FileNotFoundException
     */
    public String getRelativeFromAbsolute(String absolutePath) throws FileNotFoundException {
        String[] splitPath = absolutePath.split(ROOT_NAME);
        if (splitPath.length == 2) {
            return ROOT_NAME + splitPath[1];
        } else if (absolutePath.contains(ROOT_NAME)) {
            return ROOT_NAME;
        }
        throw new FileNotFoundException("No root name detected");
    }

    public File getRoot() {
        return root;
    }

    public File getFileByName(String filename) {
        return new File(root.getParentFile().getAbsolutePath(), filename);
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

    public void setProcessingData(boolean value) {
        processingData = value;
    }

    public boolean getProcessingData() {
        return processingData;
    }

    public List<FileListener> getSingleListeners() {
        return singleListeners;
    }

    public List<FileListener> getCollectionListeners() {
        return collectionListeners;
    }

    /**
     * Add a file to the cached synced files
     *
     * @param filepath - this should be the relative path to the root
     */
    public void addSyncedFile(String filepath) {
        if (syncedFiles != null) {
            syncedFiles.add(filepath);
        }
    }

    public Set<String> getSyncedFiles() {
        return syncedFiles;
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
