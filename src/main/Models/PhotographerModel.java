package main.Models;

import java.time.LocalDate;

public interface PhotographerModel {
    /**
     * Database primary key
     */
    int getID();
    void setID(int value);

    /**
     * Firstname, including middle name
     */
    String getFirstName();
    void setFirstName(String value);

    /**
     * Lastname
     */
    String getLastName();
    void setLastName(String value);

    /**
     * Birthday
     */
    LocalDate getBirthDay();
    void setBirthDay(LocalDate value);

    /**
     * Notes
     */
    String getNotes();
    void setNotes(String value);
}
