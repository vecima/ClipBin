package clipbin;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

/**
 * This class is derived from this tutorial by thenewboston: https://www.youtube.com/watch?v=HFAsMWkiLvg
 */
public class ConfirmBox {

	static boolean answer;

	public static boolean display(String title, String message) {
		Stage window = new Stage();

		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);

		Label label = new Label();
		label.setText(message);

		Button yesButton = new Button("Yes");
		yesButton.setOnAction(e -> {
			answer = true;
			window.close();
		});

		Button noButton = new Button("No");
		noButton.setOnAction(e -> {
			answer = false;
			window.close();
		});

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(8));
		HBox buttonLayout = new HBox(10);
		buttonLayout.getChildren().addAll(yesButton, noButton);
		buttonLayout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(label, buttonLayout);
		layout.setAlignment(Pos.CENTER);

		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();

		return answer;
	}
}