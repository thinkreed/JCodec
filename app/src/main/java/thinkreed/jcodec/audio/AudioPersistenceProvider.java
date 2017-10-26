package thinkreed.jcodec.audio;

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
        dataOutputStream.writeBytes(WavFileHeader.CHUNK_ID);
        dataOutputStream.write(intToByteArray(wavFileHeader.getChunkSize()), 0, 4);
        dataOutputStream.writeBytes(WavFileHeader.FORMAT);
        dataOutputStream.writeBytes(WavFileHeader.SUBCHUNK1_ID);
        dataOutputStream.write(intToByteArray(WavFileHeader.SUBCHUNK1_SIZE), 0, 4);
        dataOutputStream.write(shortToByteArray(wavFileHeader.getAudioFormat()), 0, 2);
        dataOutputStream.write(shortToByteArray(wavFileHeader.getNumChannels()), 0, 2);
        dataOutputStream.write(intToByteArray(wavFileHeader.getSampleRate()), 0, 4);
        dataOutputStream.write(intToByteArray(wavFileHeader.getByteRate()), 0, 4);
        dataOutputStream.write(shortToByteArray(wavFileHeader.getBlockAlign()), 0, 2);
        dataOutputStream.write(shortToByteArray(wavFileHeader.getBitsPerSample()), 0, 2);
        dataOutputStream.writeBytes(WavFileHeader.SUBCHUNK2_ID);
        dataOutputStream.write(intToByteArray(wavFileHeader.getSubChunk2Size()), 0, 4);
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

    private static byte[] shortToByteArray(short data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
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
