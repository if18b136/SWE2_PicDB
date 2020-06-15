package main.PresentationModels;

import javafx.beans.property.*;
import main.Models.Photographer;

import java.time.LocalDate;

// validation happens in the business layer
public class Photographer_PM {
    private IntegerProperty ID = new SimpleIntegerProperty();
    private StringProperty firstName = new SimpleStringProperty();
    private StringProperty lastName = new SimpleStringProperty();
    private ObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>();
    private StringProperty notes = new SimpleStringProperty();
    private Photographer model = new Photographer();

    public Photographer_PM(Photographer model) {
        refreshData(model);
    }

    public void refreshData(Photographer model) {
        this.model = model;
        this.ID.set(model.getID());
        this.firstName.set(model.getFirstName());
        this.lastName.set(model.getLastName());
        this.birthday.set(model.getBirthDay());
        this.notes.set(model.getNotes());
    }

    public IntegerProperty IDProperty() { return ID; }
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public ObjectProperty<LocalDate> birthdayProperty() { return birthday; }
    public StringProperty notesProperty() { return notes; }
    public Photographer getPhotographer () { return this.model; }

}
