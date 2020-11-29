package clipbin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

	private static final String CLIPBIN_PATH = ".clipbin";
	private static final String CLIPS_PATH = "clips";

	private EventRouter eventRouter;
	private UserInterface userInterface;
	private ClipBin clipBin;
	private boolean listenToClipboard;
	private Thread clipboardListeningThread;
	private Clip lastClipboardContents;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setOnCloseRequest(e -> close(primaryStage, e));

		this.eventRouter = new EventRouter();
		this.userInterface = new UserInterface(this.eventRouter, primaryStage);

		Path path = Paths.get(System.getProperty("user.home"), CLIPBIN_PATH, CLIPS_PATH);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException x) {
				System.err.format("Couldn't create directory: %s", path.toString());
				System.err.format("IOException: %s%n", x);
			}
		}

		this.clipBin = new ClipBin(this.eventRouter, path);

		this.listenToClipboard = true;
		this.lastClipboardContents = ClipboardManager.getClipboardContents();
		this.clipboardListeningThread = new Thread(() -> {
			while (this.listenToClipboard) {
				try {
					Thread.sleep(500);

					Clip clip = ClipboardManager.getClipboardContents();						
					boolean duplicate = false;
					if (this.lastClipboardContents != null && this.lastClipboardContents.getDisplay(0).equals(clip.getDisplay(0)))
						duplicate = true;

					if (!duplicate) {
						for (Clip existingClip : this.clipBin.getClipList()) {
							//System.out.println("checking " + clip.getDisplay(0) + " against " + existingClip.getDisplay(0));
							if (clip.getDisplay(0).equals(existingClip.getDisplay(0)))
								duplicate = true;
						}
					}

					if (!duplicate) {
						if (clip.save(path)) {
							this.lastClipboardContents = clip;
							Event addedClipEvent = new Event(EventType.ADD_CLIP, clip);
							eventRouter.sendEvent(addedClipEvent);
						} else {
							AlertBox.display("Error", "The clip could not be saved. ClipBin has stopped watching the clipboard and should be restarted.");
							this.listenToClipboard = false;
						}
					}
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
			System.out.println("Clipboard listening Thread exiting.");
		});
		this.clipboardListeningThread.setDaemon(true);
		this.clipboardListeningThread.start();
	}

	private void close(Stage stage, javafx.event.Event event) {
		event.consume();
		boolean result = ConfirmBox.display("Confirm", "Are you sure you want to exit ClipBin?");
		if (result) {
			this.listenToClipboard = false;
			System.out.println("Closing App...");
			stage.close();
		}
	}
}
