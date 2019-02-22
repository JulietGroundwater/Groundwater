package uk.ac.cam.cl.juliet.tasks;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotData3D;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator3D;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.Datapoint;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** A class for running the three-dimensional processing on a different thread */
public class ProcessingTask extends AsyncTask<Void, Void, List<Datapoint>> {
    private IProcessingCallback listener;
    private List<Datapoint> datapoints = new ArrayList<>();
    private List<SingleOrManyBursts> singles;

    public ProcessingTask(IProcessingCallback task) {
        try {
            singles = InternalDataHandler.getInstance().getSelectedData().getListOfBursts();
            listener = task;
        } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Overridden background task to process the data - currently performs this on singleton bursts
     * until memory usage gets sorted
     *
     * @param voids
     * @return Returns the processed data
     */
    @Override
    protected List<Datapoint> doInBackground(Void... voids) {
        List<PlotData3D> dataSets = new ArrayList<>();
        PlotDataGenerator3D pdg;
        for (SingleOrManyBursts single : singles) {
            List<Burst> singleton = new ArrayList<>();
            try {
                singleton.add(single.getSingleBurst());
            } catch (SingleOrManyBursts.AccessManyBurstsAsSingleException e) {
                e.printStackTrace();
            }
            // Currently performing a single burt at a time because of memory constraints
            pdg = new PlotDataGenerator3D(singleton);
            dataSets.add(pdg.getPowerPlotData());
            singleton.clear();
        }

        // Convert to datapoints for JSON serialisation later
        for (int set = 0; set < dataSets.size(); set++) {
            PlotData3D current = dataSets.get(set);
            for (int y = 0; y < current.getYValues().size(); y++) {
                datapoints.add(
                        new Datapoint(
                                set,
                                current.getYValues().get(y),
                                current.getZValues().get(0).get(y)));
            }
        }
        return datapoints;
    }

    @Override
    protected void onPostExecute(List<Datapoint> datapoint) {
        super.onPostExecute(datapoint);
        // Notify listener that the execution has returned
        listener.onTaskCompleted(datapoint);
    }
}
