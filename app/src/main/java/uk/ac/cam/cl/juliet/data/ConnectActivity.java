package uk.ac.cam.cl.juliet.data;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.microsoft.identity.client.*;
import java.util.List;
import uk.ac.cam.cl.juliet.R;

public class ConnectActivity extends AppCompatActivity implements IAuthenticationCallback {
    private User user;
    private Handler handler;
    private Button connectButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use the connect layout
        setContentView(R.layout.activity_connect);

        // Connect the button and progress bar
        connectButton = findViewById(R.id.connect_button);
        progressBar = findViewById(R.id.connect_progress);

        // Set the global Connect Activity
        Connect.getInstance().setConnectActivityInstanceInstance(this);

        // Set up action onClick
        connectButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgress();
                        connect();
                    }
                });
    }

    private void showProgress() {}

    private void showMessage(final String msg) {
        getHandler()
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConnectActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        });
    }

    private Handler getHandler() {
        if (handler == null) {
            return new Handler(ConnectActivity.this.getMainLooper());
        }

        return handler;
    }

    private void connect() {
        // Get the Authentication Manager Instance
        AuthenticationManager authManager = AuthenticationManager.getInstance();

        // Get the public client application
        PublicClientApplication clientApp = authManager.getPublicClient();

        // Try and access the users
        List<User> users = null;

        try {
            users = clientApp.getUsers();
            if (users != null && users.size() == 1) {
                // There is a cached user so silently login
                authManager.acquireTokenSilently(users.get(0), true, this, this);
            } else {
                // There are no cached users so interactively login
                authManager.acquireToken(this, this);
            }
        } catch (MsalClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PublicClientApplication clientApp = AuthenticationManager.getInstance().getPublicClient();
        if (clientApp != null) {
            clientApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSuccess(AuthenticationResult res) {
        user = res.getUser();
        String name = user.getName();
        System.out.println(res.getUser());
        // Prepare for Data Transfer Activity
        Intent dataTransferActivity = new Intent(ConnectActivity.this, DataTransferActivity.class);
        startActivity(dataTransferActivity);
    }

    @Override
    public void onError(MsalException msalException) {
        showMessage(msalException.getMessage());
    }

    @Override
    public void onCancel() {
        showMessage("User cancelled the request");
    }
}
