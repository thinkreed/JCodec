package thinkreed.jcodec;

import org.junit.Assert;
import org.junit.Test;

import thinkreed.jcodec.audio.AudioPersistenceProvider;

/**
 * Created by thinkreed on 2017/10/22.
 */
public class AudioPersistenceProviderTest {
    @Test
    public void prepareToSaveAudioData() throws Exception {
        AudioPersistenceProvider.getInstance().prepareToSaveAudioData("/sdcard/Music/t1.pcm");
        Assert.assertEquals(true, AudioPersistenceProvider.getInstance().getDataOutputStream() !=
                null);
    }

}