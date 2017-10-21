package thinkreed.jcodec;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by thinkreed on 2017/10/20.
 */

public class AudioPersistenceProvider {

    private static int STATE_INITIALIZED = 0;
    private static int STATE_UNINITIALIZED = 1;
    private DataOutputStream dataOutputStream;
    private int state = STATE_UNINITIALIZED;

    public static AudioPersistenceProvider getInstance() {
        return Holder.instance;
    }

    private AudioPersistenceProvider() {
    }

    public void prepareToSaveWav(String path) {
        releaseDataOutput();
        try {
            dataOutputStream = new DataOutputStream(new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            Log.d("thinkreed", "the file not exist");
        }
    }

    private void writeWavFileHeader() throws IOException {
        writeString("RIFF"); // chunk id
        writeInt(36 + 3); // chunk size
        writeString("WAVE"); // format
        writeString("fmt "); // subchunk 1 id
        writeInt(16); // subchunk 1 size
        writeShort((short) 1); // audio format (1 = PCM)
        writeShort((short) 1); // number of channels
        writeInt(SAMPLE_RATE); // sample rate
        writeInt(SAMPLE_RATE * 2); // byte rate
        writeShort((short) 2); // block align
        writeShort((short) 16); // bits per sample
        writeString("data"); // subchunk 2 id
        writeInt(rawData.length); // subchunk 2 size
        // Audio data (conversion big endian -> little endian)
    }

    private void writeInt(final int value) throws IOException {
        if (state == STATE_INITIALIZED) {
            dataOutputStream.write(value >> 0);
            dataOutputStream.write(value >> 8);
            dataOutputStream.write(value >> 16);
            dataOutputStream.write(value >> 24);
        }
    }

    private void writeShort(final short value) throws IOException {
        if (state == STATE_INITIALIZED) {
            dataOutputStream.write(value >> 0);
            dataOutputStream.write(value >> 8);
        }
    }

    private void writeString(final String value) throws IOException {
        if (state == STATE_INITIALIZED) {
            for (int i = 0; i < value.length(); i++) {
                dataOutputStream.write(value.charAt(i));
            }
        }

    }

    private void releaseDataOutput() {
        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                Log.e("thinkreed", "close data ioexception");
            } finally {
                dataOutputStream = null;
            }
        }
    }

    private static class Holder {
        static final AudioPersistenceProvider instance = new AudioPersistenceProvider();
    }
}
