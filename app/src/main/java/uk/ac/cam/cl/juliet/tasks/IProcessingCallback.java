package uk.ac.cam.cl.juliet.tasks;

import java.util.List;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotData3D;
import uk.ac.cam.cl.juliet.models.Datapoint;

/** Callback interface for the processing task */
public interface IProcessingCallback {
    void onTaskCompleted(
            List<Datapoint> result, List<PlotData3D> dataset, boolean isLive, boolean isLast);
}
