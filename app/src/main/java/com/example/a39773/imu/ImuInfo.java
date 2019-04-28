package com.example.a39773.imu;

public class ImuInfo {

    private long timestamp;

    private String alphaX;

    private String alphaY;

    private String alphaZ;

    private String omegaX;

    private String omegaY;

    private String omegaZ;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAlphaX() {
        return alphaX;
    }

    public void setAlphaX(String alphaX) {
        this.alphaX = alphaX;
    }

    public String getAlphaY() {
        return alphaY;
    }

    public void setAlphaY(String alphaY) {
        this.alphaY = alphaY;
    }

    public String getAlphaZ() {
        return alphaZ;
    }

    public void setAlphaZ(String alphaZ) {
        this.alphaZ = alphaZ;
    }

    public String getOmegaX() {
        return omegaX;
    }

    public void setOmegaX(String omegaX) {
        this.omegaX = omegaX;
    }

    public String getOmegaY() {
        return omegaY;
    }

    public void setOmegaY(String omegaY) {
        this.omegaY = omegaY;
    }

    public String getOmegaZ() {
        return omegaZ;
    }

    public void setOmegaZ(String omegaZ) {
        this.omegaZ = omegaZ;
    }

    @Override
    public String toString() {
        return "ImuInfo{" +
                "timestamp=" + timestamp +
                ", alphaX='" + alphaX + '\'' +
                ", alphaY='" + alphaY + '\'' +
                ", alphaZ='" + alphaZ + '\'' +
                ", omegaX='" + omegaX + '\'' +
                ", omegaY='" + omegaY + '\'' +
                ", omegaZ='" + omegaZ + '\'' +
                '}';
    }
}
