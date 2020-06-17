package main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

//import javafx.scene.layout.VBoxBuilder;

public final class Utils {
	public static final boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static final String toRGBCode(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255),
				(int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
	}

	public static final void showMessageBox(Stage parent, String title, String message) {
		final Stage dialogStage = new Stage();
		dialogStage.initOwner(parent);
		dialogStage.initModality(Modality.WINDOW_MODAL);

		final Button btn = new Button("OK");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialogStage.close();
			}
		});
		dialogStage.setTitle(title);

		VBox newVBox = new VBox();
		Text newText = new Text(message);
		newVBox.getChildren().add(newText);
		newVBox.getChildren().add(btn);
		newVBox.alignmentProperty().setValue(Pos.CENTER);
		newVBox.setSpacing(10);
		newVBox.setPadding(new Insets(5));
		dialogStage.setScene(new Scene(newVBox));

//		dialogStage.setScene(new Scene(VBoxBuilder.create()
//				.children(new Text(message), btn)
//				.alignment(Pos.CENTER).spacing(10).padding(new Insets(5))
//				.build()));
		dialogStage.show();
	}
}
