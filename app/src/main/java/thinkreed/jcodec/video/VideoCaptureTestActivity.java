package thinkreed.jcodec.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;

import thinkreed.jcodec.R;

/**
 * Created by thinkreed on 2017/10/26.
 */

public class VideoCaptureTestActivity extends AppCompatActivity{

    private TextureView previewView;
    private View btnCapture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_test);
        previewView = (TextureView) findViewById(R.id.preview_view);
        btnCapture = findViewById(R.id.btn_capture);
    }
}
