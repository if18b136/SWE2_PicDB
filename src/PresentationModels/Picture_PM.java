package PresentationModels;

import Database.DBConnection;
import Models.EXIF;
import Models.IPTC;
import Models.IPTCModel;
import Models.Picture;
import javafx.beans.property.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Picture_PM {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
//    private ObjectProperty<IPTC_PM> iptc = new SimpleObjectProperty<>();
    private IPTC_PM iptc = new IPTC_PM(new IPTC());
    private ObjectProperty<List<EXIF_PM>> exifList = new SimpleObjectProperty<>();
    private Picture model;

    public Picture_PM(Picture model) {
        this.model = model;
        id.set(model.getID());
        name.set((model.getName()));
//        iptc.set(new IPTC_PM(model.getIPTC()));
        exifList.set(mToPm(model.getExifList()));
    }

    public int getID() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public IPTC_PM getIptc() {
        return iptc;
        //return iptc == null ? new IPTC() : iptc.get();
    }

    public List<EXIF_PM> getExifList() {
        return exifList.get();
    }

    // due to me not wanting to make a fixed variation of exifs, I need to convert the exif list into a Presentation model list
    public List<EXIF_PM> mToPm(List<EXIF> exifList) {
        List<EXIF_PM> exifPmList = new ArrayList<>();
        for(EXIF exif : exifList) {
            exifPmList.add(new EXIF_PM(exif));
        }
        return exifPmList;
    }

    public EXIF_PM getExifByIndex(int index) {
        //I did not find a good way to set the default value for the exif choice box, so I had to catch the -1 first value and return the initial exif for it.
        return index == -1 ? exifList.get().get(0) : exifList.get().get(index);   // first get to receive exif list, second one to receive certain pm with index.
    }

    public void updateIptc() {
        iptc.save(model.getIPTC());
        refresh(model);
    }

    public void refresh(Picture model) {
        this.model = model;
        iptc.refreshIptc(model.getIPTC());
    }

}
