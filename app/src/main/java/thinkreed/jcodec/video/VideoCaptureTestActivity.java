package thinkreed.jcodec.video;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;

import android.view.View.OnClickListener;
import thinkreed.jcodec.R;

/**
 * Created by thinkreed on 2017/10/26.
 */

public class VideoCaptureTestActivity extends AppCompatActivity implements SurfaceTextureListener{

    private TextureView previewView;
    private View btnCapture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_test);
        previewView = (TextureView) findViewById(R.id.preview_view);
        btnCapture = findViewById(R.id.btn_capture);
        btnCapture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoCapture.getInstance().takePicture();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!previewView.isAvailable()) {
            previewView.setSurfaceTextureListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        VideoCapture.getInstance().destroy();
        super.onDestroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        VideoCapture.getInstance().prepare(previewView);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
