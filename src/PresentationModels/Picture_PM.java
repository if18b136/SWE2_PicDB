package PresentationModels;

import Database.DBConnection;
import Models.EXIF;
import Models.IPTC;
import Models.IPTCModel;
import Models.Picture;
import javafx.beans.property.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Picture_PM {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<IPTC> iptc = new SimpleObjectProperty<>();
    private ObjectProperty<List<EXIF>> exifList = new SimpleObjectProperty<>();

    public Picture_PM(Picture model) {
        id.set(model.getID());
        name.set((model.getName()));
        iptc.set(model.getIPTC());
        exifList.set(model.getExifList());
    }

    public int getID() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public IPTC getIptc() {
        return iptc.get();
        //return iptc == null ? new IPTC() : iptc.get();
    }

    public List<EXIF> getExifList() {
        return exifList.get();
    }

    public void setToCurrent(Picture model) {
        id.set(model.getID());
        name.set((model.getName()));
        iptc.set(model.getIPTC());
        exifList.set(model.getExifList());
    }
}
