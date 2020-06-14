package main.Models;

import java.time.LocalDate;

public interface CameraModel {
    /**
     * Database primary key
     */
    int getID();
    void setID(int value);

    /**
     * Name of the producer
     */
    String getProducer();
    void setProducer(String value);

    /**
     * Name of camera
     */
    String getMake();
    void setMake(String value);

    /**
     * Optional: date, when the camera was bought
     */
    LocalDate getBoughtOn();
    void setBoughtOn(LocalDate value);

    /**
     * Notes
     */
    String getNotes();
    void setNotes(String value);

    /**
     * Max ISO Value for good results. 0 means "not defined"
     */
    double getISOLimitGood();
    void setISOLimitGood(double value);
    /**
     * Max ISO Value for acceptable results. 0 means "not defined"
     */
    double getISOLimitAcceptable();
    void setISOLimitAcceptable(double value);
}
