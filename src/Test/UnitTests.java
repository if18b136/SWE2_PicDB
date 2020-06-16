package Test;

import main.Database.DAL;
import main.Database.DALFactory;
import main.Models.Photographer;
import main.Models.Picture;
import main.PresentationModels.Photographer_PM;
import main.PresentationModels.Picture_PM;
import main.Service.BusinessLayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {

    @BeforeEach
    public void setUp() {

    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void Mock() {

    }

    @Test
    public void getPictureTest() throws Exception {
        DALFactory.useMock();
        DAL dal = DALFactory.getDAL();
        Picture picture = dal.getPicture("Test");
        assertNotNull(picture);
        assertEquals(1, picture.getID());
        assertEquals(picture.getName(),"Test");
    }

    @Test
    public void retrievePhotographersTest() throws Exception {
        DALFactory.useMock();
        DAL dal = DALFactory.getDAL();
        List<Photographer_PM> photographerPmList = dal.retrievePhotographers();
        assert(photographerPmList != null);
        assert(photographerPmList.size() == 3);
    }

    @Test
    public void addNewPhotographerTest() throws Exception {
        DALFactory.useMock();
        DAL dal = DALFactory.getDAL();
        Photographer photographer = dal.addNewPhotographer("Test", "Testperson", LocalDate.of(2020,5,10),"Notes for test here");
        assertNotNull(photographer);
        assert(photographer.getID() == 1);
        assert(photographer.getFirstName().equals("Test"));
        assert(photographer.getLastName().equals("Testperson"));
        assert(photographer.getBirthDay().equals(LocalDate.of(2020,5,10)));
        assert(photographer.getNotes().equals("Notes for test here"));
    }

    @Test
    public void editPhotographerTest() throws Exception {
        DALFactory.useMock();
        DAL dal = DALFactory.getDAL();
        Photographer photographer = dal.addNewPhotographer("Test", "Testperson", LocalDate.of(2020,5,10),"Notes for test here");
        Photographer editedPhotographer = dal.editPhotographer(4,photographer.getFirstName(),photographer.getLastName(),photographer.getBirthDay(),photographer.getNotes());
        assertNotNull(editedPhotographer);
        assertNotEquals(photographer.getID(),editedPhotographer.getID());
        assertEquals(photographer.getFirstName(),editedPhotographer.getFirstName());
        assertEquals(photographer.getLastName(),editedPhotographer.getLastName());
        assertEquals(photographer.getBirthDay(),editedPhotographer.getBirthDay());
        assertEquals(photographer.getNotes(),editedPhotographer.getNotes());
    }

    @Test
    public void addNewPictureTest() throws Exception {
        DALFactory.useMock();
        DAL dal = DALFactory.getDAL();
        Picture picture = dal.addNewPicture("TestName", "1", "TestMaker","TestModel");
        assertNotNull(picture);
        assertEquals("TestName", picture.getName());
        assertNotNull(picture.getExifList());
        assertNotNull(picture.getIPTC());
    }

    @Test
    public void addNewPictureContainsExifTest() throws Exception {
        DALFactory.useMock();
        DAL dal = DALFactory.getDAL();
        Picture picture = dal.addNewPicture("TestName", "1", "TestMaker","TestModel");
        assertEquals("TestModel",picture.getExifList().get(2).getDescription());
        assertEquals("TestMaker",picture.getExifList().get(1).getDescription());
        assertEquals("1",picture.getExifList().get(0).getDescription());
    }

    @Test
    public void getAllPictureNamesTest() throws Exception {
        DALFactory.useMock();
        DAL dal = DALFactory.getDAL();
        HashMap<Integer, String> pictureNames = dal.getAllPictureNames();
        assertNotNull(pictureNames);
        assertEquals("Cat",pictureNames.get(1));
        assertEquals("Dog",pictureNames.get(2));
        assertEquals("Chicken",pictureNames.get(3));
    }

    @Test
    public void createPictureModelTest() throws Exception {
        DALFactory.useMock();
        DAL dal = DALFactory.getDAL();
        Picture_PM picturePm = dal.createPictureModel(1,"Test");
        assertNotNull(picturePm);
        assertEquals(1,picturePm.getID());
        assertEquals("Test",picturePm.getName());
    }

    @Test
    public void createPictureModelContainsExifTest() throws Exception {
        DALFactory.useMock();
        DAL dal = DALFactory.getDAL();
        Picture_PM picturePm = dal.createPictureModel(1,"Test");
        assertNotNull(picturePm);
        assertEquals("1/100",picturePm.getExifByIndex(0).getDescription());
        assertEquals("CANON",picturePm.getExifByIndex(1).getDescription());
        assertEquals("XPS 100",picturePm.getExifByIndex(2).getDescription());
    }

    @Test
    public void BusinessLayerInstanceTest() {
        BusinessLayer bl = BusinessLayer.getInstance();
        assertNotNull(bl);
    }

    @Test
    public void BusinessLayerGetPictureFolderPathFromConfigTest() {
        BusinessLayer bl = BusinessLayer.getInstance();
        String path = bl.getPath();
        assertNotNull(path);
    }

    @Test
    public void BusinessLayerCheckIfPathExistsTest() {
        BusinessLayer bl = BusinessLayer.getInstance();
        String pathString = bl.getPath();
        assertNotNull(pathString);
        Path path = Paths.get(pathString);
        assertTrue(Files.exists(path));
    }

    @Test
    public void BusinessLayerValidatePhotographerNotNullTest() {
        BusinessLayer bl = BusinessLayer.getInstance();
        assertFalse(bl.validatePhotographer(null,null,null,null));
    }

    @Test
    public void BusinessLayerValidatePhotographerIncorrectFirstNameLengthTest() {
        BusinessLayer bl = BusinessLayer.getInstance();
        String incorrectFirstName = String.format("%0" + 101 + "d", 0);
        String lastName = String.format("%0" + 50 + "d", 0);
        LocalDate birthday = LocalDate.of(1,1,1);
        String notes = "";
        assertFalse(bl.validatePhotographer(incorrectFirstName,lastName,birthday,notes));
    }

    @Test
    public void BusinessLayerValidatePhotographerIncorrectLastNameLengthTest() {
        BusinessLayer bl = BusinessLayer.getInstance();
        String firstName = String.format("%0" + 100 + "d", 0);
        String incorrectLastName = String.format("%0" + 51 + "d", 0);
        LocalDate birthday = LocalDate.of(1,1,1);
        String notes = "";
        assertFalse(bl.validatePhotographer(firstName,incorrectLastName,birthday,notes));
    }

    @Test
    public void BusinessLayerValidatePhotographerNoLastNameTest() {
        BusinessLayer bl = BusinessLayer.getInstance();
        String firstName = String.format("%0" + 100 + "d", 0);
        String incorrectLastName = "";
        LocalDate birthday = LocalDate.of(1,1,1);
        String notes = "";
        assertFalse(bl.validatePhotographer(firstName,incorrectLastName,birthday,notes));
    }

    @Test
    public void BusinessLayerValidatePhotographerIncorrectDateTest() {
        BusinessLayer bl = BusinessLayer.getInstance();
        String firstName = String.format("%0" + 100 + "d", 0);
        String lastName = String.format("%0" + 50 + "d", 0);
        LocalDate futureDate = LocalDate.of(3000,1,1);
        String notes = "";
        assertFalse(bl.validatePhotographer(firstName,lastName,futureDate,notes));
    }

    @Test
    public void BusinessLayerValidatePhotographerValidTest() {
        BusinessLayer bl = BusinessLayer.getInstance();
        String firstName = String.format("%0" + 100 + "d", 0);
        String lastName = String.format("%0" + 50 + "d", 0);
        LocalDate birthday = LocalDate.of(1,1,1);
        String notes = "";
        assertTrue(bl.validatePhotographer(firstName,lastName,birthday,notes));
    }

}
