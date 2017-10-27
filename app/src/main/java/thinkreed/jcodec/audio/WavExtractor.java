package thinkreed.jcodec.audio;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by thinkreed on 2017/10/25.
 */

public class WavExtractor {

    private static class Holder {
        static final WavExtractor instance = new WavExtractor();
    }

    public static final int STATE_INITIALIZED = 0;
    public static final int STATE_UNINITIALIZED = 1;
    private WavFileHeader wavFileHeader;
    private DataInputStream dataInputStream;
    private FileInputStream fileInputStream;
    private byte[] intField = new byte[4];
    private byte[] shortField = new byte[2];
    private int audioMinBufferSize;
    private PcmFrame pcmFrame;
    private int state = STATE_UNINITIALIZED;

    private WavExtractor() {

    }

    public static WavExtractor getInstance() {
        return Holder.instance;
    }

    public void prepare(String path) {
        release();
        try {
            fileInputStream = new FileInputStream(path);
            dataInputStream = new DataInputStream(fileInputStream);
            readWavFileHeader();
            audioMinBufferSize = AudioTrack.getMinBufferSize(wavFileHeader.getSampleRate(),
                    wavFileHeader.getNumChannels() == 1 ? AudioFormat.CHANNEL_OUT_MONO :
                            AudioFormat.CHANNEL_OUT_STEREO, wavFileHeader.getBitsPerSample() ==
                            16 ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT);
            pcmFrame = PcmFrame.create(audioMinBufferSize);
            state = STATE_INITIALIZED;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public PcmFrame advance() {

        if (state != STATE_INITIALIZED) {
            return PcmFrame.create(1);
        }

        try {
            int bytesRead = dataInputStream.read(pcmFrame.getData(), 0, audioMinBufferSize);
            pcmFrame.setSize(bytesRead > 0 ? bytesRead : -1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("thinkreed", "read frame error");
            pcmFrame.setSize(-1);
        }
        return pcmFrame;
    }

    public void release() {
        try {
            if (fileInputStream != null) {
                fileInputStream.close();
            }

            if (dataInputStream != null) {
                dataInputStream.close();
            }

            state = STATE_UNINITIALIZED;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("thinkreed", "close input stream error");
        } finally {
            pcmFrame = null;
            fileInputStream = null;
            dataInputStream = null;
        }

    }

    public int getState() {
        return this.state;
    }

    private void readWavFileHeader() {
        wavFileHeader = WavFileHeader.create();
        try {
            // ChunkID
            read4Bytes();

            // ChunkSize
            dataInputStream.read(intField);
            wavFileHeader.setChunkSize(byteArrayToInt(intField));

            // Format
            read4Bytes();

            // SubChunk1ID
            read4Bytes();

            // SubChunk1Size
            dataInputStream.read(intField);
            byteArrayToInt(intField);

            // AudioFormat
            dataInputStream.read(shortField);

            // NumChannels
            dataInputStream.read(shortField);
            wavFileHeader.setNumChannels(byteArrayToShort(shortField));

            // SampleRate
            dataInputStream.read(intField);
            wavFileHeader.setSampleRate(byteArrayToInt(intField));

            // ByteRate
            dataInputStream.read(intField);

            // BlockAlign
            dataInputStream.read(shortField);

            // BitsPerSample
            dataInputStream.read(shortField);
            wavFileHeader.setBitsPerSample(byteArrayToShort(shortField));

            // SubChunk2ID
            read4Bytes();

            // SubChunk2Size
            dataInputStream.read(intField);
            wavFileHeader.setSubChunk2Size(byteArrayToInt(intField));

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("thinkreed", "io exception when read wav file header");
        }

    }

    private void read4Bytes() throws IOException {
        for (int i = 0; i < 4; i++) {
            dataInputStream.readByte();
        }
    }

    private static short byteArrayToShort(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private static int byteArrayToInt(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
}
