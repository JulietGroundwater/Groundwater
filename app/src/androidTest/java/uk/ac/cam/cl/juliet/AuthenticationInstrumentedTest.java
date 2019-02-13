package uk.ac.cam.cl.juliet;

import static org.junit.Assert.*;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.ac.cam.cl.juliet.data.AuthenticationManager;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AuthenticationInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("uk.ac.cam.cl.juliet", appContext.getPackageName());
    }

    @Test
    public void getInstance_never_null() {
        AuthenticationManager auth = AuthenticationManager.getInstance();
        assertNotNull(auth);
    }
}
