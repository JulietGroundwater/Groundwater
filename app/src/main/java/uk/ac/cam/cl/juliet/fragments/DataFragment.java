package uk.ac.cam.cl.juliet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.microsoft.graph.authentication.IAuthenticationAdapter;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.data.AuthenticationManager;
import uk.ac.cam.cl.juliet.data.ConnectActivity;

/**
 * Fragment for the 'data' screen.
 *
 * @author Ben Cole
 */
public class DataFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AuthenticationManager authManager = AuthenticationManager.getInstance();
        authManager.createAuthenticator(getActivity().getApplication());
        IAuthenticationAdapter authAdaptor = authManager.getAuthAdapter();

        authAdaptor.login(getActivity(), new ICallback<Void>() {
            @Override
            public void success(Void aVoid) {
                System.out.println("SUCCESS");
            }

            @Override
            public void failure(ClientException ex) {
                System.out.println("FAILURE");
            }
        });

        Intent intent = new Intent(getActivity(), ConnectActivity.class);
        startActivity(intent);

        return inflater.inflate(R.layout.fragment_data, container, false);
    }
}
