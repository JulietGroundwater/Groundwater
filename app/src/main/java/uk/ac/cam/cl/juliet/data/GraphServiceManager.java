package uk.ac.cam.cl.juliet.data;

import android.util.Log;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.core.DefaultClientConfig;
import com.microsoft.graph.core.IClientConfig;
import com.microsoft.graph.extensions.GraphServiceClient;
import com.microsoft.graph.extensions.IGraphServiceClient;
import com.microsoft.graph.http.IHttpRequest;
import java.io.IOException;

public class GraphServiceManager implements IAuthenticationProvider {
    private final String TAG = "GraphSerivceManager";
    private static GraphServiceManager INSTANCE;
    private static IGraphServiceClient graphServiceClient;

    public static synchronized GraphServiceManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraphServiceManager();
        }

        return INSTANCE;
    }

    /**
     * Takes the request and add authentication header to it
     *
     * @param request
     */
    @Override
    public void authenticateRequest(IHttpRequest request) {
        String token = null;
        try {
            token = AuthenticationManager.getInstance().getAccessToken();
            request.addHeader("Authorization", "Bearer " + token);
            Log.i(TAG, "Request: " + request);
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized IGraphServiceClient getGraphServiceClient() {
        return getGraphServiceClient(this);
    }

    public synchronized IGraphServiceClient getGraphServiceClient(
            IAuthenticationProvider provider) {
        if (graphServiceClient == null) {
            IClientConfig clientConfig =
                    DefaultClientConfig.createWithAuthenticationProvider(provider);
            graphServiceClient =
                    new GraphServiceClient.Builder().fromConfig(clientConfig).buildClient();
        }

        return graphServiceClient;
    }
}
