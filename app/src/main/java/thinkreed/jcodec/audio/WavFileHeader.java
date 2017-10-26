package thinkreed.jcodec.audio;

/**
 * Created by thinkreed on 2017/10/23.
 */

public class WavFileHeader {

    public static final int WAV_FILE_HEADER_SIZE = 44;
    public static final int WAV_CHUNKSIZE_EXCLUDE_DATA = 36;
    public static final int WAV_CHUNKSIZE_OFFSET = 4;
    public static final int WAV_SUB_CHUNKSIZE1_OFFSET = 16;
    public static final int WAV_SUB_CHUNKSIZE2_OFFSET = 40;

    public static final String CHUNK_ID = "RIFF";
    public static final String FORMAT = "WAVE";
    public static final String SUBCHUNK1_ID = "fmt ";
    public static final int SUBCHUNK1_SIZE = 16;
    public static final String SUBCHUNK2_ID = "data";
    public static final short audioFormat = 1;
    private short numChannels;
    private int sampleRate;
    private int chunkSize;
    private short bitsPerSample;
    private int subChunk2Size;

    public static WavFileHeader create() {
        return new WavFileHeader();
    }

    private WavFileHeader() {
    }

    public short getAudioFormat() {
        return audioFormat;
    }

    public short getNumChannels() {
        return numChannels;
    }

    public void setNumChannels(short numChannels) {
        this.numChannels = numChannels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getByteRate() {
        return sampleRate * numChannels * bitsPerSample / 8;
    }

    public short getBlockAlign() {
        return (short) (numChannels * bitsPerSample / 8);
    }

    public short getBitsPerSample() {
        return bitsPerSample;
    }

    public void setBitsPerSample(short bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
    }

    public int getSubChunk2Size() {
        return subChunk2Size;
    }

    public void setSubChunk2Size(int subChunk2Size) {
        this.subChunk2Size = subChunk2Size;
    }
}
