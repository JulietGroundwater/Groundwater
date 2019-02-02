package uk.ac.cam.cl.juliet.data;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.extensions.DriveItem;
import uk.ac.cam.cl.juliet.R;

public class DataTransferActivity {
    private final String TAG = "DataManager";
    private final GraphServiceController graphServiceController = new GraphServiceController();

    public void sendData(String filename, String fileExtension, byte[] data) {
        graphServiceController.uploadDatafile(data, new ICallback< DriveItem >() {

            @Override
            public void success(DriveItem driveItem) {
                Log.d(TAG, "Successful");
            }

            @Override
            public void failure(ClientException ex) {
                Log.e(TAG, "Error: " + ex.getMessage());
            }
        });
    }
}
