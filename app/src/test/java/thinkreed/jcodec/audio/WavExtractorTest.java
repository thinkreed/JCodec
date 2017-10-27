package thinkreed.jcodec.audio;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by thinkreed on 2017/10/27.
 */
public class WavExtractorTest {
    
    @Test
    public void prepare () throws Exception {
        
        Assert.assertTrue(WavExtractor.getInstance().getState() == WavExtractor.STATE_UNINITIALIZED);
        WavExtractor.getInstance().prepare("/sdcard/Music/test.wav");
        Assert.assertTrue(WavExtractor.getInstance().getState() == WavExtractor.STATE_INITIALIZED);
    }
    
    @Test
    public void advance () throws Exception {
    
    }
    
    @Test
    public void release () throws Exception {
    
    }
    
}