package PresentationModels;

import Models.EXIF;
import Models.IPTC;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EXIF_PM {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();

    public EXIF_PM(EXIF model) {
        System.out.println("Test EXIF PM constructor");
        if(model == null){ model = new EXIF();}
        id.set(model.getID());
        name.set((model.getName()));
        description.set(model.getDescription());
    }

    public String getName() {
        return name.get();
    }

    public String getDescription() {
        return description.get();
    }

}
