package clipbin;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Application;
import javafx.stage.Stage;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

public class App extends Application {

	private static final String CLIPBIN_PATH = ".clipbin";
	private static final String CLIPS_PATH = "clips";

	private EventRouter eventRouter;
	private ClipBin clipBin;
	private UserInterface userInterface;

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
		//this.eventRouter.registerHandler(null, this); // null type means all events

		this.clipBin = new ClipBin(this.eventRouter, path);

		// COPY Key Combo
		Set<Integer> copyKeys = new HashSet<Integer>();
		copyKeys.add(NativeKeyEvent.VC_CONTROL);
		copyKeys.add(NativeKeyEvent.VC_C);
		KeyComboListener copyKeyComboListener = new KeyComboListener(copyKeys, new KeyTriggerable()
		{
			@Override
			public void trigger()
			{
				try
				{
					Thread.sleep(100);
					Clip clip = ClipboardManager.getClipboardContents();
					
					boolean duplicate = false;
					for (Clip existingClip : App.this.clipBin.getClipList())
					{
						//System.out.println("checking " + clip.getDisplay(0) + " against " + existingClip.getDisplay(0));
						if (clip.getDisplay(0).equals(existingClip.getDisplay(0)))
							duplicate = true;
					}

					if (!duplicate)
					{
						if (clip.save(path)) {
							Event addedClipEvent = new Event(EventType.ADD_CLIP, clip);
							eventRouter.sendEvent(addedClipEvent);
						}
					}
				}
				catch (InterruptedException ie)
				{
					ie.printStackTrace();
				}
			}
		});

	}

	private static void close(Stage stage, javafx.event.Event event) {
		event.consume();
		boolean result = ConfirmBox.display("Confirm", "Are you sure you want to exit ClipBin?");
		if (result) {
			System.out.println("Closing...");

			try
			{
				if (GlobalScreen.isNativeHookRegistered())
					GlobalScreen.unregisterNativeHook();
			}
			catch (NativeHookException nhe)
			{
				System.err.println("There was a problem unregistering the native hook.");
				System.err.println(nhe.getMessage());

				nhe.printStackTrace();
			}
			finally
			{
				System.out.println("ClipBin closed.");
			}

			stage.close();
			//System.exit(0);
		}
	}
}
