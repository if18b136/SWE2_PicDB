package Models;

public class EXIF {
    // TODO get various data types in, currently only using Strings for EXIF
    private int ID = -1;
    private String name = "";
    private String description = "";

    public int getID() { return ID; }
    public void setID(int id) { this.ID = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}
