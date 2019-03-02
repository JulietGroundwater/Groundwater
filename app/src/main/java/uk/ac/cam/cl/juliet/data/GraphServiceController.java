package uk.ac.cam.cl.juliet.data;

import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.extensions.DriveItem;
import com.microsoft.graph.extensions.IGraphServiceClient;

/** Handles all requests being made using the Microsoft Graph API */
public class GraphServiceController {
    private final IGraphServiceClient graphServiceClient;
    private static final String EXPAND_OPTIONS_FOR_CHILDREN = "children";

    public GraphServiceController() {
        graphServiceClient = GraphServiceManager.getInstance().getGraphServiceClient();
    }

    /**
     * A useful wrapper for checking if the one drive has the root_name folder
     *
     * @param callback
     */
    public void hasGroundwaterFolder(ICallback<DriveItem> callback) {
        getFolder(InternalDataHandler.ROOT_NAME, callback);
    }

    /**
     * Get the folder and the children inside using the expand options
     *
     * @param relativePath the relative path from the root of one drive (groundwater/collection/...)
     * @param callback the callback to return the <code>DriveItem</code> to
     */
    public void getFolder(String relativePath, ICallback<DriveItem> callback) {
        graphServiceClient
                .getMe()
                .getDrive()
                .getRoot()
                .getItemWithPath(relativePath)
                .buildRequest()
                .expand(EXPAND_OPTIONS_FOR_CHILDREN)
                .get(callback);
    }

    /**
     * Once authenticated, this method can be used to upload data to a one drive account
     *
     * @param relativePath from the root directory for the app (groundwater)
     * @param data
     * @param callback
     */
    public void uploadDatafile(String relativePath, byte[] data, ICallback<DriveItem> callback) {
        graphServiceClient
                .getMe()
                .getDrive()
                .getRoot()
                .getItemWithPath(relativePath)
                .getContent()
                .buildRequest()
                .put(data, callback);
    }
}
