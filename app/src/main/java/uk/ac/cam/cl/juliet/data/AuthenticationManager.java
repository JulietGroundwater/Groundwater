package uk.ac.cam.cl.juliet.data;

import android.app.Activity;
import android.app.Application;
import android.os.OperationCanceledException;
import android.util.Log;
import com.microsoft.graph.authentication.IAuthenticationAdapter;
import com.microsoft.graph.authentication.MSAAuthAndroidAdapter;
import com.microsoft.identity.client.*;
import java.io.IOException;

public class AuthenticationManager extends Application {
    public static final String[] SCOPES = {"openid","Files.ReadWrite","User.ReadBasic.All"};
    private final String TAG = "AuthenticationManager";
    private static AuthenticationManager INSTANCE;
    private static PublicClientApplication PUBLIC_CLIENT;

    private AuthenticationResult authResult;
    private IAuthenticationCallback activityCallback;

    private AuthenticationManager() {}

    public static AuthenticationManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticationManager();
            if (PUBLIC_CLIENT == null) {
                PUBLIC_CLIENT = new PublicClientApplication(Connect.getInstance());
            }
        }
        return INSTANCE;
    }

    public static void reset() {
        INSTANCE = null;
    }

    /**
     * Get the access token from the private authentication result
     *
     * @return <code>String</code> of the token
     * @throws AuthenticatorException
     * @throws IOException
     * @throws OperationCanceledException
     */
    public String getAccessToken()
            throws AuthenticatorException, IOException, OperationCanceledException {
        return authResult.getAccessToken();
    }

    public void disconnect() {
        // Remove the current user from the client
        PUBLIC_CLIENT.remove(authResult.getUser());
        // Reset the authentication manager
        AuthenticationManager.reset();
    }

    /**
     * @param activity
     * @param callback
     */
    public void acquireToken(Activity activity, IAuthenticationCallback callback) {
        // Set the activity callback
        activityCallback = callback;
        // Make call to acquire token using the public client
        PUBLIC_CLIENT.acquireToken(activity, SCOPES, getAuthInteractiveCallback());
    }

    /**
     * Acquiring the token silently
     *
     * @param user The cached user information
     * @param forceRefresh
     * @param callback
     */
    public void acquireTokenSilently(
            User user, Boolean forceRefresh, IAuthenticationCallback callback) {
        // Set the activity callback
        activityCallback = callback;
        // Make call to acquire token using the public client
        PUBLIC_CLIENT.acquireTokenSilentAsync(
                SCOPES, user, null, forceRefresh, getAuthSilentCallback());
    }

    /**
     * The callback used for interactive requests - no checking of cache and we use the token with
     * MS Graph API for calls
     *
     * @return <code>AuthenticationCallback</code>
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                // Log the successful authentication
                Log.d(TAG, "Successful Authentication");
                Log.d(TAG, "Token: " + authenticationResult.getAccessToken());

                // Store the authentication result
                authResult = authenticationResult;

                if (activityCallback != null) {
                    activityCallback.onSuccess(authResult);
                }
            }

            @Override
            public void onError(MsalException exception) {
                // Log failed authentication
                Log.e(TAG, "Failed Authentication: " + exception.getMessage());

                // Pass callback through to the activity callback
                if (activityCallback != null) {
                    activityCallback.onError(exception);
                }
            }

            @Override
            public void onCancel() {
                // Log cancellation
                Log.d(TAG, "User cancelled authentication");

                // Pass callback through
                if (activityCallback != null) {
                    activityCallback.onCancel();
                }
            }
        };
    }

    public AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                Log.d(TAG, "Successful Authentication");
                Log.d(TAG, authenticationResult.getAccessToken());
                authResult = authenticationResult;

                // Pass on the callback to the result (Data Transfer)
                if (activityCallback != null) {
                    activityCallback.onSuccess(authResult);
                }
            }

            @Override
            public void onError(MsalException exception) {
                // Log failed authentication
                Log.e(TAG, "Failed Authentication: " + exception.getMessage());

                // Pass callback through to the activity callback
                if (activityCallback != null) {
                    activityCallback.onError(exception);
                }
            }

            public void onError(Exception exc) {
                System.out.println(exc.getMessage());
            }

            @Override
            public void onCancel() {
                // Log cancellation
                Log.d(TAG, "User cancelled authentication");

                // Pass callback through
                if (activityCallback != null) {
                    activityCallback.onCancel();
                }
            }
        };
    }

    public PublicClientApplication getPublicClient() {
        return PUBLIC_CLIENT;
    }
}
