package uk.ac.cam.cl.juliet.data;

import android.app.Application;
import android.support.v4.app.FragmentActivity;

/** For passing the contexts around */
public class Connect extends Application {
    private static Connect CONNECT_INSTANCE;
    private static FragmentActivity CONNECT_ACTIVITY_INSTANCE;

    @Override
    public void onCreate() {
        CONNECT_INSTANCE = this;
        super.onCreate();
    }

    public static Connect getInstance() {
        return CONNECT_INSTANCE;
    }

    public void setConnectActivityInstanceInstance(FragmentActivity activity) {
        CONNECT_ACTIVITY_INSTANCE = activity;
    }
}
