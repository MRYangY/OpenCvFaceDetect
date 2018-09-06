package com.example.root.opencvfacedetect;

public class CameraRawData {
    private byte[] rawData;
    private long timestamp;

    public CameraRawData() {
    }

    public CameraRawData(byte[] rawData, long timestamp) {
        this.rawData = rawData;
        this.timestamp = timestamp;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
