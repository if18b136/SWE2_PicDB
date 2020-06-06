package Models;

public class EXIF {
    private int ID;
    // TODO get various data types in, currently only using Strings for EXIF
    private String name;
    private String description;
    private int pictureID;  // needed?

    public int getID() { return ID; }
    public void setID(int id) { this.ID = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}
