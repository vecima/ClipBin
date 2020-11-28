package clipbin;

import java.io.InputStream;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserInterface implements EventHandler {

	private EventRouter eventRouter;
	private static javafx.scene.image.Image deleteImage;
	private Pane clipListContent;
	
	public UserInterface(EventRouter eventRouter, Stage primaryStage) throws Exception {
		this.eventRouter = eventRouter;
		this.eventRouter.registerHandler(null, this); // null type means all events

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		InputStream deleteImageFileInputStream = classLoader.getResourceAsStream("delete_16.png");
		//FileInputStream deleteImageFileInputStream = new FileInputStream("resources/delete_16.png");
		deleteImage = new javafx.scene.image.Image(deleteImageFileInputStream);
		deleteImageFileInputStream.close();

		primaryStage.setTitle("ClipBin");

		Label label1 = new Label("Welcome to the first scene!");

		/*
		Button alertButton = new Button("Alert");
		alertButton.setOnAction(e -> {
			boolean result = ConfirmBox.display("Confirm", "Are you sure?");
			System.out.println("result: " + result);
		});

		Button closeButton = new Button("Close");
		closeButton.setOnAction(e -> close(e));
		*/
		HBox topContent = new HBox();
		Label topLabel = new Label("Top");
		topContent.getChildren().add(topLabel);

		clipListContent = new VBox(8);
		clipListContent.setPadding(new Insets(8));
		Label centerLabel = new Label("Center");
		clipListContent.getChildren().add(centerLabel);

		//refreshClips(clipBin.getClipList());

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(topContent);
		borderPane.setCenter(clipListContent);

		Scene scene = new Scene(borderPane, 400, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void addClip(Clip clip) {
		Platform.runLater(() -> {
			HBox clipContent = new HBox(4);
			clipContent.setId(clip.getDisplay(0));
			//Label clipLabel = new Label(clip.getDisplay(50));
			//HBox.setHgrow(clipLabel, Priority.ALWAYS);
			Button clipButton = new Button(clip.getDisplay(0));
			clipButton.setMaxWidth(Double.MAX_VALUE);
			clipButton.setAlignment(Pos.BASELINE_LEFT);
			clipButton.setOnAction(e -> {
				ClipboardManager.setClipboardContents(clip, null);
			});
			HBox.setHgrow(clipButton, Priority.ALWAYS);
			Button deleteButton = new Button();
			deleteButton.setMinSize(20, 20);
			deleteButton.setPrefSize(20, 20);
			deleteButton.setMaxSize(20, 20);
			ImageView imageView = new ImageView(deleteImage);
			imageView.setFitHeight(16);
			imageView.setPreserveRatio(true);
			deleteButton.setGraphic(imageView);
			deleteButton.setOnAction(e -> {
				boolean result = ConfirmBox.display("Confirm", "Are you sure you want to delete this Clip?");
				if (result) {
					System.out.println("starting delete clip thread from UI: " + clip.getDisplay(0));

					// longrunning operation runs on different thread
					Thread thread = new Thread(() -> {
						//Thread.sleep(1000);
						if (clip.delete()) {
							// send an event
							this.eventRouter.sendEvent(new Event(EventType.REMOVE_CLIP, clip));
						}
					});
					// don't let thread prevent JVM shutdown
					thread.setDaemon(true);
					thread.start();
				}
			});
			deleteButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent; -fx-font-size: 1em; /* 12 */ -fx-text-fill: #828282;");
			clipContent.getChildren().addAll(clipButton, deleteButton);
			clipListContent.getChildren().add(clipContent);
		});
	}

	private void removeClip(Clip clip) {
		Platform.runLater(() -> {
			Node childToRemove = null;
			for (Node child : clipListContent.getChildren()) {
				if (child.getId() != null && child.getId().equals(clip.getDisplay(0)))
					childToRemove = child;
			}
			if (childToRemove != null)
				clipListContent.getChildren().remove(childToRemove);
		});
	}

	private void refreshClips(List<Clip> clipList) {
		Platform.runLater(() -> {
			clipListContent.getChildren().clear();
			clipList.forEach(c -> addClip(c));
		});
	}

	@Override
	public void handle (Event event) {
		if (EventType.ADD_CLIP.equals(event.getEventType())) {
			addClip(event.getClip());
		} else if (EventType.REMOVE_CLIP.equals(event.getEventType())) {
			removeClip(event.getClip());
		}
	}
}