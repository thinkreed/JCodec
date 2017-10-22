package thinkreed.jcodec;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
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
    private FileOutputStream fileOutputStream;
    private int state = STATE_UNINITIALIZED;

    public static AudioPersistenceProvider getInstance() {
        return Holder.instance;
    }

    private AudioPersistenceProvider() {
    }

    public void prepareToSaveAudioData(String path) {
        releaseDataOutput();
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(path);
            dataOutputStream = new DataOutputStream(fileOutputStream);
        } catch (FileNotFoundException e) {
            Log.e("thinkreed", "the file not exist");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("thinkreed", "create file failed");
        }
    }

    private void writeWavFileHeader() throws IOException {
    }

    private void writeInt(final int value) throws IOException {
        if (getState() == STATE_INITIALIZED) {
            getDataOutputStream().write(value >> 0);
            getDataOutputStream().write(value >> 8);
            getDataOutputStream().write(value >> 16);
            getDataOutputStream().write(value >> 24);
        }
    }

    private void writeShort(final short value) throws IOException {
        if (getState() == STATE_INITIALIZED) {
            getDataOutputStream().write(value >> 0);
            getDataOutputStream().write(value >> 8);
        }
    }

    private void writeString(final String value) throws IOException {
        if (getState() == STATE_INITIALIZED) {
            for (int i = 0; i < value.length(); i++) {
                getDataOutputStream().write(value.charAt(i));
            }
        }

    }

    public void releaseDataOutput() {
        if (getDataOutputStream() != null) {
            try {
                getDataOutputStream().close();
                fileOutputStream.close();
            } catch (IOException e) {
                Log.e("thinkreed", "close data ioexception");
            } finally {
                dataOutputStream = null;
                fileOutputStream = null;
            }
        }
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public int getState() {
        return state;
    }

    private static class Holder {
        static final AudioPersistenceProvider instance = new AudioPersistenceProvider();
    }
}
