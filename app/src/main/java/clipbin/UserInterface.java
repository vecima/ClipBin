package clipbin;

import java.io.InputStream;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
	private Image deleteImage;
	private Pane clipListContent;
	private StringProperty currentBufferContentsString = new SimpleStringProperty();
	
	public UserInterface(EventRouter eventRouter, Stage primaryStage) throws Exception {
		this.eventRouter = eventRouter;
		this.eventRouter.registerHandler(null, this); // null type means all events

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		InputStream deleteImageFileInputStream = classLoader.getResourceAsStream("delete_16.png");
		this.deleteImage = new Image(deleteImageFileInputStream);
		deleteImageFileInputStream.close();

		primaryStage.setTitle("ClipBin");
		HBox topContent = new HBox();
		// TODO create buttons to alter the clip layout, and bring up an "options" view
		Button listButton = new Button("List");
		listButton.setMaxWidth(Double.MAX_VALUE);
		listButton.setAlignment(Pos.BASELINE_LEFT);
		listButton.setOnAction(e -> {
			//TODO display as a list (default)
		});
		HBox.setHgrow(listButton, Priority.ALWAYS);
		Button gridButton = new Button("Grid");
		gridButton.setMaxWidth(Double.MAX_VALUE);
		gridButton.setAlignment(Pos.BASELINE_LEFT);
		gridButton.setOnAction(e -> {
			//TODO display as a grid
		});
		HBox.setHgrow(gridButton, Priority.ALWAYS);
		Button optionsButton = new Button("Options");
		optionsButton.setMaxWidth(Double.MAX_VALUE);
		optionsButton.setAlignment(Pos.BASELINE_LEFT);
		optionsButton.setOnAction(e -> {
			//TODO open options view
		});
		HBox.setHgrow(optionsButton, Priority.ALWAYS);
		topContent.getChildren().addAll(listButton, gridButton, optionsButton);

		ScrollPane centerContentScrollPane = new ScrollPane();
		centerContentScrollPane.pannableProperty().set(true);
		centerContentScrollPane.fitToWidthProperty().set(true);
		this.clipListContent = new VBox(8);
		this.clipListContent.setPadding(new Insets(8));
		centerContentScrollPane.setContent(this.clipListContent);

		VBox bottomContent = new VBox(8);
		bottomContent.setPadding(new Insets(8));
		Label currentBufferContentsLabel = new Label("Current Buffer Contents:");
		Label currentBufferContentsText = new Label("");
		currentBufferContentsText.textProperty().bind(this.currentBufferContentsString);
		bottomContent.getChildren().addAll(currentBufferContentsLabel, currentBufferContentsText);

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(topContent);
		borderPane.setCenter(centerContentScrollPane);
		borderPane.setBottom(bottomContent);

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
			ImageView imageView = new ImageView(this.deleteImage);
			imageView.setFitHeight(16);
			imageView.setPreserveRatio(true);
			deleteButton.setGraphic(imageView);
			deleteButton.setOnAction(e -> {
				boolean result = ConfirmBox.display("Confirm", "Are you sure you want to delete this Clip?");
				if (result) {
					//System.out.println("starting delete clip thread from UI: " + clip.getDisplay(0));
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
			this.clipListContent.getChildren().add(clipContent);
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
				this.clipListContent.getChildren().remove(childToRemove);
		});
	}

	private void updateCurrentBufferContents(Clip clip) {
		Platform.runLater(() -> {
			System.out.println("handling content change in ui");
			if (clip != null)
				this.currentBufferContentsString.set(clip.getDisplay(0));
		});
	}

	@Override
	public void handle (Event event) {
		if (EventType.ADD_CLIP.equals(event.getEventType())) {
			this.addClip(event.getClip());
		} else if (EventType.REMOVE_CLIP.equals(event.getEventType())) {
			this.removeClip(event.getClip());
		} else if (EventType.CLIPBOARD_CHANGED.equals(event.getEventType())) {
			this.updateCurrentBufferContents(event.getClip());
		}
	}
}