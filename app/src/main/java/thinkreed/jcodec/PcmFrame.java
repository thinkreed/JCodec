package thinkreed.jcodec;

/**
 * Created by thinkreed on 2017/10/25.
 */

public class PcmFrame {

    public static PcmFrame create(int frameSize) {
        return new PcmFrame(frameSize);
    }

    private PcmFrame(int frameSize) {
        data = new byte[frameSize];
    }

    private int size;
    private byte[] data;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }
}
