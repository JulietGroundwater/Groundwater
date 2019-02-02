package uk.ac.cam.cl.juliet.data;

import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.extensions.DriveItem;
import com.microsoft.graph.extensions.IGraphServiceClient;

/** Handles all requests being made using the Microsoft Graph API */
public class GraphServiceController {
    private final IGraphServiceClient graphServiceClient;

    public GraphServiceController() {
        graphServiceClient = GraphServiceManager.getInstance().getGraphServiceClient();
    }

    public void checkForFile(ICallback<DriveItem> callback) {
        graphServiceClient.getMe().getDrive().getRoot().buildRequest().get(callback);
    }

    /**
     * Once authenticated, this method can be used to upload data to a one drive account
     *
     * @param filename
     * @param fileExtension
     * @param data
     * @param callback
     */
    public void uploadDatafile(String filename, String fileExtension, byte[] data, ICallback<DriveItem> callback) {
        graphServiceClient
                .getMe()
                .getDrive()
                .getRoot()
                .getItemWithPath(filename + "." + fileExtension)
                .getContent()
                .buildRequest()
                .put(data, callback);
    }
}
