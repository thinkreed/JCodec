package thinkreed.jcodec.video;

/**
 * Created by thinkreed on 2017/10/26.
 */

public class VideoCapture {

    public static VideoCapture getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        static final VideoCapture instance = new VideoCapture();
    }

    private VideoCapture() {

    }

}
