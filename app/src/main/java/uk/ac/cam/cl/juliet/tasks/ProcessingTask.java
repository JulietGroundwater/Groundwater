package uk.ac.cam.cl.juliet.tasks;

import android.os.AsyncTask;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.cam.cl.juliet.computationengine.Burst;
import uk.ac.cam.cl.juliet.computationengine.InvalidBurstException;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotData3D;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator3D;
import uk.ac.cam.cl.juliet.data.InternalDataHandler;
import uk.ac.cam.cl.juliet.models.Datapoint;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** A class for running the three-dimensional processing on a different thread */
public class ProcessingTask extends AsyncTask<Void, Void, List<Datapoint>> {
    private final int BATCH_SIZE = 5;
    private IProcessingCallback listener;
    private List<Datapoint> datapoints = new ArrayList<>();
    private List<SingleOrManyBursts> singles;

    public ProcessingTask(IProcessingCallback task) {
        try {
            generateBursts();
            singles = InternalDataHandler.getInstance().getSelectedData().getListOfBursts();
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
        File file = idh.getFileByName(idh.getSelectedData().getNameToDisplay());
        SingleOrManyBursts many = idh.getSelectedData();
        for (int i = 0; i < file.listFiles().length; i++) {
            File innerFile = file.listFiles()[i];
            try {
                many.getListOfBursts().get(i).setSingleBurst(new Burst(innerFile, 1));
            } catch (SingleOrManyBursts.AccessSingleBurstAsManyException e) {
                e.printStackTrace();
            }
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
        List<Burst> batchList = new ArrayList<>();

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
            dataSets.add(pdg.getPowerPlotData());
            batchList.clear();
        }

        for (int set = 0; set < dataSets.size(); set++) {
            for (int x = 0; x < dataSets.get(set).getXValues().size(); x++) {
                System.out.println(dataSets.get(set).getXValues().get(x));
            }
        }

        // Convert time to natural numbers for the x-axis
        Map<Double, Integer> converter = new HashMap<>();
        int count = 1;
        for (int set = 0; set < dataSets.size(); set++) {
            PlotData3D current = dataSets.get(set);
            for (int x = 0; x < current.getXValues().size(); x++) {
                if (!converter.containsKey(current.getXValues().get(x))) {
                    converter.put(current.getXValues().get(x), count);
                    count++;
                }
            }
        }

        // Convert to datapoints for JSON serialisation later
        for (int set = 0; set < dataSets.size(); set++) {
            PlotData3D current = dataSets.get(set);
            for (int x = 0; x < current.getXValues().size(); x++) {
                for (int y = 0; y < current.getYValues().size(); y++) {
                    datapoints.add(
                            new Datapoint(
                                    converter.get(current.getXValues().get(x)),
                                    current.getYValues().get(y),
                                    current.getZValues().get(0).get(y)));
                }
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
