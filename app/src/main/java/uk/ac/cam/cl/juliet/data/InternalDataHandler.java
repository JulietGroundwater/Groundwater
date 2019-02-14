package uk.ac.cam.cl.juliet.data;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InternalDataHandler {

    private static InternalDataHandler INSTANCE;
    private static final String ROOT_NAME = "groundwater";
    private static File root;

    public static InternalDataHandler getInstance() {
        if (INSTANCE == null) {
          INSTANCE = new InternalDataHandler();
          root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT_NAME);
        }
        return INSTANCE;
    }

    public List<String> getCollectionOfFiles(String dirName) {
        List<String> list = new ArrayList<>();
        File dir = new File(root.getAbsolutePath(), dirName);
        for(File f : dir.listFiles()) {
          if (f.getName().contains("DAT")) {
              list.add(f.getName());
          }
        }

        return list;
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
}
