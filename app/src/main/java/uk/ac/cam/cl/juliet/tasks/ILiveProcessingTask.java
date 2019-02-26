package uk.ac.cam.cl.juliet.tasks;

import java.util.List;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/** Simple interface that allows passing of the data for live processing */
public interface ILiveProcessingTask extends IProcessingCallback {
    void receiveSingleOrManyBursts(List<SingleOrManyBursts> bursts);
}
