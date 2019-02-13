package uk.ac.cam.cl.juliet.data;

import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalException;

public interface IAuthenticationCallback {
    void onSuccess(AuthenticationResult res);

    void onError(MsalException msalException);

    void onCancel();
}
