package thinkreed.jcodec;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.concurrent.Future;

/**
 * Created by thinkreed on 2017/10/18.
 */

public class AudioCapture {
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private int audioMinBufferSize;
    private Future task;
    private byte[] audioBuffer;
    private AudioRecord audioRecord;

    public static AudioCapture getInstance() {
        return new AudioCapture();
    }

    private AudioCapture() {
        audioMinBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE, AudioFormat
                .CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, audioMinBufferSize);
        audioBuffer = new byte[audioMinBufferSize];
    }

    public void start() {
        audioRecord.startRecording();
        task = TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getPcmFrame();
            }
        });
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
    }

    public void stop() {
        audioRecord.stop();
        TaskExecutor.getInstance().cancel(task);
    }
}
