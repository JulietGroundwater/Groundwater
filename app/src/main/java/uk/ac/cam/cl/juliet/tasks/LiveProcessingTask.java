package uk.ac.cam.cl.juliet.tasks;

import android.os.AsyncTask;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotData3D;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator3D;
import uk.ac.cam.cl.juliet.models.MultipleBurstsDataTypes;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** A task for handling the live processing of data */
public class LiveProcessingTask extends AsyncTask<Void, Void, List<PlotData3D>> {
    // REMOVE BATCHES - last file and current file
    private List<IProcessingCallback> listeners;
    private File previousFile;
    private File currentFile;
    private SingleOrManyBursts parent;
    private List<PlotDataGenerator3D> generators = new ArrayList<>();
    private List<SingleOrManyBursts> singleOrManyBurstsList = new ArrayList<>();
    private boolean lastFile;
    private MultipleBurstsDataTypes type;

    public LiveProcessingTask(
            List<IProcessingCallback> tasks,
            File previousFile,
            File currentFile,
            SingleOrManyBursts parent,
            boolean lastFile,
            MultipleBurstsDataTypes type) {
        this.listeners = tasks;
        this.previousFile = previousFile;
        this.currentFile = currentFile;
        this.parent = parent;
        this.lastFile = lastFile;
        this.type = type;
    }

    @Override
    protected List<PlotData3D> doInBackground(Void... voids) {
        // Files will not have any SingleOrManyBurst object associated with them so need to be
        // generated
        PlotDataGenerator3D pdg = null;
        List<Burst> burstList = new ArrayList<>();
        List<PlotData3D> datasets = new ArrayList<>();
        try {
            // Add new SingleOrManyBurst to be passed out later for storage
            Burst prevBurst = new Burst(this.previousFile, 1);
            singleOrManyBurstsList.add(new SingleOrManyBursts(prevBurst, this.previousFile, false));
            // For phase data we need the previous burst and the current burst to calculate the
            // difference
            burstList.add(prevBurst);
            Burst currBurst = new Burst(this.currentFile, 1);
            burstList.add(currBurst);
            pdg = new PlotDataGenerator3D(burstList);
            generators.add(pdg);
        } catch (InvalidBurstException e) {
            e.printStackTrace();
        }
        return datasets;
    }

    @Override
    protected void onPostExecute(List<PlotData3D> datasets) {
        super.onPostExecute(datasets);
        for (IProcessingCallback listener : this.listeners) {
            listener.onTaskCompleted(generators, true, this.lastFile);
            // Also notify to do this task of accumulating the bursts over time
            if (listener instanceof ILiveProcessingTask && singleOrManyBurstsList.size() > 0) {
                ((ILiveProcessingTask) listener).receiveSingleOrManyBursts(singleOrManyBurstsList);
            }
        }
    }
}
