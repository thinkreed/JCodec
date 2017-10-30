package thinkreed.jcodec.video;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Log;

/**
 * Created by thinkreed on 2017/10/26.
 */

public class VideoCapture {

    private CameraManager manager;
    private String cameraId;

    public static VideoCapture getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {

        static final VideoCapture INSTANCE = new VideoCapture();
    }

    private VideoCapture() {

    }

    public void prepare(Context context) {

        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        getAvailableCamera();
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
}
