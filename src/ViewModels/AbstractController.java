package ViewModels;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class AbstractController implements Initializable {
    // the stage we want to present our Controller on.
    private Stage stage;

    // getter for public Stage access.
    public Stage getStage() { return stage; }
    // setter for public Stage setting.
    public void setStage(Stage stage) { this.stage = stage; }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
    }
    // the three dots means that zero or more String objects or an array of them may be passed as the argument.
    private void show(String resource, Object model, String title, Modality m, String... cssList) throws IOException {
        // new fxmlLoader
        FXMLLoader fl = new FXMLLoader();
        // get the fxml file
        fl.setLocation(getClass().getResource(resource));
        fl.load();
        // parent is the javafx base class for nodes that have child in the scene graph. It handles all hierachical scene graph operations (add/remove child, branching,...).
        Parent root = fl.getRoot();

        // Enum StageStyle defines the possible styles for a Stage. Decorated defines a normal stage style with a solid white background and plattform decorations.
        Stage newStage = new Stage(StageStyle.DECORATED);
        // A Stage can optionally have an owner Window. The owner then acts as the stages parent, if the parent/owner is closed the descendant stage will be closed also.
        newStage.initOwner(stage);
        // Modality makes Windows able to block other Windows from getting user inputs
        newStage.initModality(m);

        Scene scene = new Scene(root,1024,768);
        scene.getStylesheets().add(getClass().getResource(".//style//application.css").toExternalForm());
        // if we need extra css with code
        for (String css : cssList) {
            scene.getStylesheets().add(getClass().getResource(css).toExternalForm());
        }

        // Now we call all created objects and show the stage
        AbstractController controller = (AbstractController) fl.getController();
        controller.setStage(newStage);
        controller.setModel(model);
        newStage.setScene(scene);
        newStage.setTitle(title);
        newStage.show();
    }

    public void setModel(Object mode) {
        //optional set model in derived classes
    }

    // Initialization in private method, call in public
    public void show(String resource, Object model, String title, String... cssList) throws IOException {
        show(resource,model,title,Modality.NONE,cssList);
    }

    // here we can add an model object in the show method
    public void showDialog(String resource, Object model, String title, String... cssList) throws IOException {
        show(resource, model, title, Modality.WINDOW_MODAL, cssList);
    }

    // Without model object
    public void showDialog(String resource, String title, String... cssList) throws IOException {
        show(resource, null, title, Modality.WINDOW_MODAL, cssList);
    }
}
