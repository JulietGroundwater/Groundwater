package uk.ac.cam.cl.juliet.data;

import android.widget.Toast;

import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.core.IBaseClient;
import com.microsoft.graph.extensions.ContentType;
import com.microsoft.graph.extensions.DriveItem;
import com.microsoft.graph.extensions.Folder;
import com.microsoft.graph.extensions.IGraphServiceClient;
import com.microsoft.graph.http.BaseRequest;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.http.HttpMethod;
import com.microsoft.graph.options.Option;
import com.microsoft.identity.client.PublicClientApplication;

import java.io.IOException;
import java.util.List;

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
     * Creates a folder in the specified path
     * @param relativePath - where to create the folder
     * @param folderName - the name of the folder
     * @param callback - the callback on post completion
     */
    public void createFolder(String relativePath, String folderName, ICallback<DriveItem> callback) {
        DriveItem folder = new DriveItem();
        folder.name = folderName;
        folder.folder = new Folder();
        graphServiceClient.getMe().getDrive().getRoot().getChildren().buildRequest().post(folder, callback);
    }


    /**
     * Once authenticated, this method can be used to upload data to a one drive account
     *
     * @param relativePath from the root directory for the app (groundwater) not including the filename
     * @param data the data of the file
     * @param callback the callback on task success or failure that is called
     */
    public void uploadDatafile(final String relativePath, String folderPath, final byte[] data, final ICallback<DriveItem> callback) {
        // Check to see if there is a folder for the file
        InternalDataHandler idh = InternalDataHandler.getInstance();
        if (!idh.getSyncedFiles().contains(relativePath)) {
            String[] splitToGetFolderName = folderPath.split("/");
            this.createFolder(folderPath, splitToGetFolderName[splitToGetFolderName.length - 1], new ICallback<DriveItem>() {
                @Override
                public void success(DriveItem driveItem) {
                    graphServiceClient
                            .getMe()
                            .getDrive()
                            .getRoot()
                            .getItemWithPath(relativePath)
                            .getContent()
                            .buildRequest()
                            .put(data, callback);
                }

                @Override
                public void failure(ClientException ex) {
                    System.out.println("Failed to create a file at " + relativePath);
                    callback.failure(ex);
                    ex.printStackTrace();
                }
            });
        }
    }
}
