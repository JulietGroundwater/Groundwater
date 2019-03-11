package uk.ac.cam.cl.juliet.tasks;

import android.os.AsyncTask;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator3D;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;

/** A class for running the three-dimensional processing on a different thread */
public class ProcessingTask extends AsyncTask<Void, Void, Void> {
    private static final int BATCH_SIZE = 10;
    private IProcessingCallback listener;
    private List<PlotDataGenerator3D> generators = new ArrayList<>();
    private List<Burst> listOfBursts = new ArrayList<>();
    private File[] listFiles;

    public ProcessingTask(IProcessingCallback task) {
        InternalDataHandler idh = InternalDataHandler.getInstance();
        idh.setProcessingData(true);
        try {
            File file =
                    idh.getFileByName(
                            idh.getRelativeFromAbsolute(
                                    idh.getCollectionSelected().getFile().getAbsolutePath()));
            listFiles = file.listFiles();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        listener = task;
    }

    private void generateBursts(int from, int to) throws InvalidBurstException, IOException {
        // Generate the bursts (done here so load time is quicker)
        listOfBursts.clear();
        if (listFiles != null) {
            for (int i = from; i < to; i++) {
                if (i < listFiles.length) {
                    File innerFile = listFiles[i];
                    listOfBursts.add(new Burst(innerFile, 1));
                }
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        PlotDataGenerator3D pdg = null;
        int current = 0;
        do {
            try {
                // Only generate the bursts we need now to avoid excessive GC
                generateBursts(current, current + BATCH_SIZE - 1);
            } catch (InvalidBurstException ibe) {
                ibe.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdg = new PlotDataGenerator3D(listOfBursts);
            generators.add(pdg);
            current += BATCH_SIZE - 1;
        } while (current + 1 < listFiles.length);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onTaskCompleted(generators, false, false);
    }
}
