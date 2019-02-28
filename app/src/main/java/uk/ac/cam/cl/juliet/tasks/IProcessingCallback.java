package uk.ac.cam.cl.juliet.tasks;

import java.util.List;
import uk.ac.cam.cl.juliet.computationengine.plotdata.PlotDataGenerator3D;

/** Callback interface for the processing task */
public interface IProcessingCallback {
    void onTaskCompleted(List<PlotDataGenerator3D> generator, boolean isLive, boolean isLast);
}
