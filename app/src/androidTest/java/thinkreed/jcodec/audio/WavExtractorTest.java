package thinkreed.jcodec.audio;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.test.InstrumentationRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by thinkreed on 2017/10/29.
 */
public class WavExtractorTest {

    @Before
    public void grantPermission() {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + InstrumentationRegistry.getTargetContext().getPackageName()
                    + " android.permission.READ_EXTERNAL_STORAGE");
        }
    }

    @Test
    public void prepare() throws Exception {
        Assert.assertTrue(WavExtractor.getInstance().getState() == WavExtractor.STATE_UNINITIALIZED);
        WavExtractor.getInstance().prepare("/sdcard/Music/test.wav");
        Assert.assertTrue(WavExtractor.getInstance().getState() == WavExtractor.STATE_INITIALIZED);
    }

    @Test
    public void advance() throws Exception {
    }

    @Test
    public void release() throws Exception {
    }

}