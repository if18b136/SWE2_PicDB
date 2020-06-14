package main.Models;

public interface EXIFModel {
    /**
     * Name of camera
     */
    String getMake();
    void setMake(String value);

    /**
     * Aperture number
     */
    double getFNumber();
    void setFNumber(double value);


    /**
     * Exposure time
     */
    double getExposureTime();
    void setExposureTime(double value);

    /**
     * ISO value
     */
    double getISOValue();
    void setISOValue(double value);

    /**
     * Flash yes/no
     */
    boolean getFlash();
    void setFlash(boolean value);

    /**
     * The exposure program
     */
    ExposurePrograms getExposureProgram();
    void setExposureProgram(ExposurePrograms value);
}
