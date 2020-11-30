package clipbin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClipBin implements EventHandler
{
	private EventRouter eventRouter;
	private List<Clip> clipList;

	public ClipBin(EventRouter eventRouter, Path clipsPath) {
		this.eventRouter = eventRouter;
		this.eventRouter.registerHandler(null, this); // null type means all events

		// read files into Clip objects
		this.clipList = new ArrayList<Clip>();
		this.readInClips(clipsPath);
	}

	private void readInClips(Path path) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{clip}")) {
			Gson gson = new GsonBuilder().registerTypeAdapter(Clip.class, new ClipFactory()).create();
			for (Path filePath: stream) {
				Clip clip = gson.fromJson(Files.readString(filePath), Clip.class);
				clip.setClipPath(filePath);
				this.eventRouter.sendEvent(new Event(EventType.ADD_CLIP, clip));
			}
		} catch (IOException | DirectoryIteratorException x) {
			System.err.println(x);
		}
	}

	public List<Clip> getClipList() {
		return this.clipList;
	}

	@Override
	public void handle (Event event) {
		if (EventType.ADD_CLIP.equals(event.getEventType())) {
			this.clipList.add(event.getClip());
		} else if (EventType.REMOVE_CLIP.equals(event.getEventType())) {
			this.clipList.remove(event.getClip());
		}
	}
}