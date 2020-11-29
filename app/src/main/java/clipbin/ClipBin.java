package clipbin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClipBin implements EventHandler
{
	private EventRouter eventRouter;
	private List<Clip> clipList;

	public ClipBin(EventRouter eventRouter, Path clipsPath)
	{
		this.eventRouter = eventRouter;
		this.eventRouter.registerHandler(null, this); // null type means all events

		// read files into Clip objects
		this.clipList = new ArrayList<Clip>();
		this.readInClips(clipsPath);
	}

	private void readInClips(Path path)
	{
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{clip}")) {
			for (Path filePath: stream) {
				//TODO use java.nio
				try (BufferedReader reader = Files.newBufferedReader(filePath)) {
					FileInputStream fileIn = new FileInputStream(filePath.toFile());
					ObjectInputStream objectInputStream = new ObjectInputStream(fileIn);
					Clip clip = (Clip) objectInputStream.readObject();
					objectInputStream.close();
					fileIn.close();
					clip.setClipPath(filePath);
					//clipList.add(clip);
					this.eventRouter.sendEvent(new Event(EventType.ADD_CLIP, clip));
				} catch (IOException x) {
					System.err.format("IOException: %s%n", x);
				}
			}
		} catch (IOException | DirectoryIteratorException | ClassNotFoundException x) {
			System.err.println(x);
		}
	}

	public List<Clip> getClipList()
	{
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