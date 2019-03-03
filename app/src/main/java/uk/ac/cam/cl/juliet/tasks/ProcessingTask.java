package uk.ac.cam.cl.juliet.tasks;

import android.os.AsyncTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator3D;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** A class for running the three-dimensional processing on a different thread */
public class ProcessingTask extends AsyncTask<Void, Void, Void> {
    private static final int BATCH_SIZE = 10;
    private IProcessingCallback listener;
    private List<PlotDataGenerator3D> generators = new ArrayList<>();
    private List<SingleOrManyBursts> listOfBursts;

    public ProcessingTask(IProcessingCallback task) {
        try {
            listOfBursts =
                    InternalDataHandler.getInstance().getCollectionSelected().getListOfBursts();
            listener = task;
        } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
            e.printStackTrace();
        }
    }

    private void generateBursts(int from, int to) throws InvalidBurstException, IOException {
        // Generate the bursts (done here so load time is quicker)
        InternalDataHandler idh = InternalDataHandler.getInstance();
        File file =
                idh.getFileByName(
                        idh.getRelativeFromAbsolute(
                                idh.getCollectionSelected().getFile().getAbsolutePath()));
        SingleOrManyBursts many = idh.getCollectionSelected();
        if (file.listFiles() != null) {
            for (int i = from; i < to; i++) {
                try {
                    if (i < listOfBursts.size()) {
                        File innerFile = file.listFiles()[i];
                        many.getListOfBursts().get(i).setSingleBurst(new Burst(innerFile, 1));
                    }
                } catch (SingleOrManyBursts.AccessSingleBurstAsManyException
                        | SingleOrManyBursts.AccessManyBurstsAsSingleException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        PlotDataGenerator3D pdg = null;
        List<Burst> burstList = new ArrayList<>();
        int current = 0;

        try {
            do {
                burstList.clear();
                try {
                    // Only generate the bursts we need now to avoid excessive GC
                    generateBursts(current, current + BATCH_SIZE - 1);
                } catch (InvalidBurstException ibe) {
                    ibe.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int i = current; i < current + BATCH_SIZE - 1; i++) {
                    if (i >= listOfBursts.size()) {
                        break;
                    }
                    burstList.add(listOfBursts.get(i).getSingleBurst());
                }
                pdg = new PlotDataGenerator3D(burstList);
                generators.add(pdg);
                current += BATCH_SIZE - 1;
            } while (current + 1 < listOfBursts.size());
        } catch (SingleOrManyBursts.AccessManyBurstsAsSingleException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onTaskCompleted(generators, false, false);
    }
}
