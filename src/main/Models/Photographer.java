package main.Models;

import java.time.LocalDate;

public class Photographer implements PhotographerModel{
    private int ID = -1;
    private String firstName = "";
    private String lastName = "";
    private LocalDate birthday = LocalDate.of(1,1,1);
    private String notes = "";

    @Override
    public int getID() { return this.ID; }
    public void setID(int value) { this.ID=value; }

    @Override
    public String getFirstName() { return this.firstName; }
    public void setFirstName(String value) { this.firstName = value; }

    @Override
    public String getLastName() { return this.lastName; }
    public void setLastName(String value) { this.lastName = value; }

    @Override
    public LocalDate getBirthDay() { return this.birthday; }
    public void setBirthDay(LocalDate value) { this.birthday = value; }

    @Override
    public String getNotes() { return this.notes; }
    public void setNotes(String value) { this.notes = value; }

    public String getFullName() {
        return this.firstName.length() > 0 ? (this.firstName + " " + this.lastName) : this.lastName;
    }
}
