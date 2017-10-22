package thinkreed.jcodec;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by thinkreed on 2017/10/18.
 */

public class AudioCapture {
    private static int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};
    private static int DEFAULT_SAMPLE_RATE = 44100;
    private int audioMinBufferSize;
    private Future task;
    private byte[] audioBuffer;
    private AudioRecord audioRecord;

    public static AudioCapture getInstance() {
        return new AudioCapture();
    }

    private AudioCapture() {
        if (!initAudioRecordWithDefaultSampleRate()) {
            findAvailableAudioRecord();
        }
        audioBuffer = new byte[audioMinBufferSize];
    }

    private boolean initAudioRecordWithDefaultSampleRate() {
        audioMinBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE, AudioFormat
                .CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, audioMinBufferSize);
        return audioRecord.getState() == AudioRecord.STATE_INITIALIZED;
    }

    private void findAvailableAudioRecord() {
        for (int rate : mSampleRates) {
            tryEverySampleRate(rate);
        }
    }

    private void tryEverySampleRate(int rate) {
        for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat
                .ENCODING_PCM_16BIT}) {
            if (tryEveryAudioFormat(rate, audioFormat)) {
                return;
            }
        }
    }

    @Nullable
    private boolean tryEveryAudioFormat(int rate, short audioFormat) {
        for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat
                .CHANNEL_IN_STEREO}) {

            if (tryGetAudioRecord(rate, audioFormat, channelConfig)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private boolean tryGetAudioRecord(int rate, short audioFormat, short channelConfig) {
        try {
            Log.d("thinkreed", "Attempting rate " + rate + "Hz, bits: " + audioFormat +
                    ", channel: "
                    + channelConfig);
            audioMinBufferSize = AudioRecord.getMinBufferSize(rate, channelConfig,
                    audioFormat);

            if (audioMinBufferSize != AudioRecord.ERROR_BAD_VALUE) {
                // check if we can instantiate and have a success
                AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        rate, channelConfig,
                        audioFormat, audioMinBufferSize);

                return recorder.getState() == AudioRecord.STATE_INITIALIZED;
            }
            return false;
        } catch (Exception e) {
            Log.e("thinkreed", rate + "Exception, keep trying.", e);
            return false;
        }
    }

    public void start() {
        audioRecord.startRecording();
        AudioPersistenceProvider.getInstance().prepareToSaveAudioData("/sdcard/Music/test.pcm");
        task = TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getPcmFrame();
            }
        });
        Log.d("thinkreed", "task is null? " + task);
    }

    private void getPcmFrame() {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            dealReadState(audioRecord.read(audioBuffer, 0, audioMinBufferSize));
        }
    }

    private void dealReadState(int bytesRead) {

        if (bytesRead == AudioRecord.ERROR) {
            return;
        }

        if (bytesRead == AudioRecord.ERROR_BAD_VALUE) {
            return;
        }

        if (bytesRead == AudioRecord.ERROR_DEAD_OBJECT) {
            return;
        }

        if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION) {
            return;
        }

        consumePcmFrame(bytesRead);
    }

    private void consumePcmFrame(int pcmFrameSize) {
        Log.d("thinkreed", "get pcm frame size is " + pcmFrameSize);
        try {
            AudioPersistenceProvider.getInstance().getDataOutputStream().write(audioBuffer, 0, pcmFrameSize);
        } catch (IOException e) {
            Log.e("thinkreed", "io exception when write pcm frame");
        }
    }

    private FileOutputStream openFileOutputStream(String path) {
        return null;
    }

    public void stop() {
        audioRecord.stop();
        TaskExecutor.getInstance().cancel(task);
        audioRecord.release();
        audioRecord = null;
        AudioPersistenceProvider.getInstance().releaseDataOutput();
    }
}
