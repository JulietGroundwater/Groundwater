package uk.ac.cam.cl.juliet.tasks;

import java.util.List;
import uk.ac.cam.cl.juliet.models.Datapoint;

/** Callback interface for the processing task */
public interface IProcessingCallback {
    void onTaskCompleted(List<Datapoint> result);
}
