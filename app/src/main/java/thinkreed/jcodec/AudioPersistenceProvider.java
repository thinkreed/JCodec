package thinkreed.jcodec;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by thinkreed on 2017/10/20.
 */

public class AudioPersistenceProvider {

    private static int STATE_INITIALIZED = 0;
    private static int STATE_UNINITIALIZED = 1;
    private DataOutputStream dataOutputStream;
    private FileOutputStream fileOutputStream;
    private int state = STATE_UNINITIALIZED;
    private int dataSize = 0;
    private String wavFilePath;

    public static AudioPersistenceProvider getInstance() {
        return Holder.instance;
    }

    private AudioPersistenceProvider() {
    }

    public void prepareToSaveAudioData(String path, WavFileHeader wavFileHeader) {
        releaseDataOutput();
        try {
            wavFilePath = path;
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(path);
            dataOutputStream = new DataOutputStream(fileOutputStream);
            writeWavFileHeader(wavFileHeader);
            dataSize = 0;
        } catch (FileNotFoundException e) {
            Log.e("thinkreed", "the file not exist");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("thinkreed", "create file failed");
        }
    }

    private void writeWavFileHeader(WavFileHeader wavFileHeader) throws IOException {
        writeString(WavFileHeader.CHUNK_ID); // chunk id
        writeInt(wavFileHeader.getChunkSize()); // chunk size
        writeString(WavFileHeader.FORMAT); // format
        writeString(WavFileHeader.SUBCHUNK1_ID); // subchunk 1 id
        writeInt(WavFileHeader.SUBCHUNK1_SIZE); // subchunk 1 size
        writeShort(wavFileHeader.getAudioFormat()); // audio format (1 = PCM)
        writeShort(wavFileHeader.getNumChannels()); // number of channels
        writeInt(wavFileHeader.getSampleRate()); // sample rate
        writeInt(wavFileHeader.getByteRate()); // byte rate
        writeShort(wavFileHeader.getBlockAlign()); // block align
        writeShort(wavFileHeader.getBitsPerSample()); // bits per sample
        writeString(WavFileHeader.SUBCHUNK2_ID); // subchunk 2 id
        writeInt(wavFileHeader.getSubChunk2Size()); // subchunk 2 size
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
                writeDataSize();
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

    public void write(byte[] buffer, int count) {
        try {
            getDataOutputStream().write(buffer, 0, count);
            dataSize += count;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private void writeDataSize() {
        try {
            RandomAccessFile wavFile = new RandomAccessFile(wavFilePath, "rw");
            wavFile.seek(WavFileHeader.WAV_CHUNKSIZE_OFFSET);
            wavFile.write(intToByteArray(dataSize + WavFileHeader.WAV_CHUNKSIZE_EXCLUDE_DATA), 0,
                    4);
            wavFile.seek(WavFileHeader.WAV_SUB_CHUNKSIZE2_OFFSET);
            wavFile.write(intToByteArray(dataSize), 0, 4);
            wavFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private static byte[] intToByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    private DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public int getState() {
        return state;
    }

    private static class Holder {
        static final AudioPersistenceProvider instance = new AudioPersistenceProvider();
    }
}
