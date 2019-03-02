package uk.ac.cam.cl.juliet.tasks;

import android.os.AsyncTask;
import java.io.File;
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
            generateBursts();
            listOfBursts =
                    InternalDataHandler.getInstance().getCollectionSelected().getListOfBursts();
            listener = task;
        } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
            e.printStackTrace();
        } catch (InvalidBurstException e) {
            e.printStackTrace();
        }
    }

    private void generateBursts() throws InvalidBurstException {
        // Generate the bursts (done here so load time is quicker)
        InternalDataHandler idh = InternalDataHandler.getInstance();
        List<SingleOrManyBursts> list = new ArrayList<>();
        File file = idh.getFileByName(idh.getCollectionSelected().getNameToDisplay());
        SingleOrManyBursts many = idh.getCollectionSelected();
        if (file.listFiles() != null) {
            for (int i = 0; i < file.listFiles().length; i++) {
                File innerFile = file.listFiles()[i];
                try {
                    many.getListOfBursts().get(i).setSingleBurst(new Burst(innerFile, 1));
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
        if (listOfBursts.size() > 1) {
            try {
                do {
                    burstList.clear();
                    for (int i = current; i < current + BATCH_SIZE - 1; i++) {
                        if (i >= listOfBursts.size()) {
                            break;
                        }
                        burstList.add(listOfBursts.get(i).getSingleBurst());
                    }
                    pdg = new PlotDataGenerator3D(burstList);
                    generators.add(pdg);
                    current += BATCH_SIZE - 1;
                }while(current + 1 < listOfBursts.size());
            } catch (SingleOrManyBursts.AccessManyBurstsAsSingleException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onTaskCompleted(generators, false, false);
    }
}
