package thinkreed.jcodec.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import java.util.Arrays;

/**
 * Created by thinkreed on 2017/10/26.
 */

public class VideoCapture extends StateCallback {

    private static final int STATE_UNINITIALIZED = 0;
    private static final int STATE_CAMERA_FOUND = 1;
    private static final int STATE_CAMERA_OPENED = 2;
    private static final int STATE_CAMERA_READY = 3;
    private CameraManager manager;
    private String cameraId;
    private int state = STATE_UNINITIALIZED;
    private HandlerThread handlerThread;
    private Handler handler;
    private TextureView textureView;
    private CameraDevice cameraDevice;
    private Surface surface;
    private CaptureRequest.Builder builder;
    private CameraCaptureSession session;
    private CaptureRequest request;

    private static class Holder {

        static final VideoCapture INSTANCE = new VideoCapture();
    }

    private static class CaptureSessionStateCallback extends CameraCaptureSession.StateCallback {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            VideoCapture.getInstance().onCaptureSessionConfigured(session);
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    }

    private static class CaptureSessionCaptureCallback extends CameraCaptureSession.CaptureCallback {

    }

    private VideoCapture() {
        handlerThread = new HandlerThread("camera thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public static VideoCapture getInstance() {
        return Holder.INSTANCE;
    }

    public CameraCaptureSession getSession() {
        return session;
    }

    public void setSession(CameraCaptureSession session) {
        this.session = session;
    }

    public void onCaptureSessionConfigured(CameraCaptureSession session) {
        setSession(session);
        try {
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            request = builder.build();
            session.setRepeatingRequest(request, new CaptureSessionCaptureCallback(), handler);
            state = STATE_CAMERA_READY;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        state = STATE_CAMERA_OPENED;
        cameraDevice = camera;
        getSurface();
        createPreviewSession();
    }

    private void createPreviewSession() {
        try {
            builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CaptureSessionStateCallback(), null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void getSurface() {
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(300, 300);
        surface = new Surface(surfaceTexture);
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {

    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {

    }

    public void prepare(TextureView textureView) {
        this.textureView = textureView;
        manager = (CameraManager) textureView.getContext().getSystemService(Context.CAMERA_SERVICE);
        getAvailableCamera();
        openCamera();
    }

    public void destroy() {
        textureView = null;
        stopHandlerThread();
        closeCamera();
    }

    private void closeCamera() {
        closeAutoCloseable(session);
        closeAutoCloseable(cameraDevice);
    }

    private void closeAutoCloseable(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                autoCloseable = null;
            }
        }
    }

    private void openCamera() {
        try {
            manager.openCamera(cameraId, this, handler);
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e("thinkreed", "need permission to open camera " + e.getMessage());
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e("thinkreed", e.getMessage());
        }
    }

    private void getAvailableCamera() {
        try {
            checkEveryCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.e("thinkreed", e.getMessage());
        }
    }

    private void checkEveryCamera() throws CameraAccessException {
        for (String cameraId : manager.getCameraIdList()) {
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);

            if (isFacing(cameraCharacteristics)) {
                continue;
            }

            if (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) == null) {
                continue;
            }

            if (isFlashSupported(cameraCharacteristics)) {
                this.cameraId = cameraId;
                state = STATE_CAMERA_FOUND;
                return;
            }
        }
    }

    private boolean isFacing(CameraCharacteristics cameraCharacteristics) {
        Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
        return facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT;
    }

    private boolean isFlashSupported(CameraCharacteristics cameraCharacteristics) {
        Boolean available = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        return available != null ? available : false;
    }

    private void stopHandlerThread() {
        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
