package uk.ac.cam.cl.juliet.tasks;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotData3D;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator3D;
import uk.ac.cam.cl.juliet.models.Datapoint;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** A task for handling the live processing of data */
public class LiveProcessingTask extends AsyncTask<Void, Void, List<PlotData3D>> {
    private IProcessingCallback listener;
    private List<File> fileBatch;
    private List<Datapoint> datapoints = new ArrayList<>();
    private List<SingleOrManyBursts> singles = new ArrayList<>();
    private final int BATCH_SIZE = 1;

    public LiveProcessingTask(IProcessingCallback task, List<File> batch) {
        this.listener = task;
        this.fileBatch = batch;
    }

    @Override
    protected List<PlotData3D> doInBackground(Void... voids) {
        generateSingleOrManyBurstsFromFiles();
        PlotDataGenerator3D pdg;
        List<Burst> batchList = new ArrayList<>();
        List<PlotData3D> datasets = new ArrayList<>();
        int batches = (int) Math.floor(((double) singles.size()) / ((double) BATCH_SIZE));

        for (int batchNumber = 0; batchNumber < batches; batchNumber++) {
            for (int burstIndex = 0; burstIndex < BATCH_SIZE; burstIndex++) {
                try {
                    batchList.add(
                            singles.get((batchNumber * BATCH_SIZE) + burstIndex).getSingleBurst());
                } catch (SingleOrManyBursts.AccessManyBurstsAsSingleException e) {
                    e.printStackTrace();
                }
            }
            pdg = new PlotDataGenerator3D(batchList);
            datasets.add(pdg.getPowerPlotData());
            batchList.clear();
        }

        return datasets;
    }

    /** A function to take the files and generate the bursts and wrapper for the data */
    private void generateSingleOrManyBurstsFromFiles() {
        for (File file : this.fileBatch) {
            try {
                singles.add(new SingleOrManyBursts(new Burst(file), false, file.getName()));
            } catch (InvalidBurstException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(List<PlotData3D> datasets) {
        super.onPostExecute(datasets);
        listener.onTaskCompleted(datapoints, datasets, true);
    }
}
