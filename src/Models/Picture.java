package Models;

import java.util.ArrayList;
import java.util.List;

public class Picture{
    private int ID;
    private String name;
    private IPTC iptc;
    private List<EXIF> exifList = new ArrayList<>();

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }   // should not be changed - DB has auto increment

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public IPTC getIPTC() { return iptc; }
    public void setIPTC(IPTC iptc) { this.iptc = iptc; }

    public List<EXIF> getExifList() { return exifList; }
    public void setExifList(List<EXIF> exifList) { this.exifList = exifList; }
    public void addEXIF(EXIF exif) { this.exifList.add(exif); }
}
