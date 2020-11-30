package clipbin;

import com.google.gson.Gson;

import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public abstract class Clip
{
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");

	enum ClipType
	{
		STRING, IMAGE, FILE_LIST
	}

	protected ClipType type;
	protected String display;
	private transient Path clipPath;

	public boolean save(Path parentPath) {
		if (this.display == null)
			this.display = this.getDisplay(0);
		Path filePath = assembleFilePath(parentPath);
		try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE)) {
			Gson gson = new Gson();
			writer.write(gson.toJson(this));
			this.setClipPath(filePath);
			return true;
		} catch (IOException x) {
			x.printStackTrace();
			return false;
		}
	}

	protected Path assembleFilePath(Path parentPath) {
		return parentPath.resolve(dateFormat.format(new Date()) + ".clip");
	}

	public boolean delete() {
		try {
			Files.delete(this.clipPath);
			return true;
		} catch (IOException x) {
			x.printStackTrace();
			return false;
		}
	}

	public ClipType getClipType() {
		return this.type;
	}

	public Path getClipPath() {
		return this.clipPath;
	}

	public void setClipPath(Path pClipPath) {
		this.clipPath = pClipPath;
	}

	protected String truncate(String inputString, int maxCharacters) {
		if (maxCharacters != 0 && inputString.length() > maxCharacters)
			inputString = inputString.substring(0, Math.min(inputString.length(), maxCharacters)) + "...";
		return inputString;
	}

	public abstract String getDisplay(int pMaxCharacters);

	public abstract Object getData();

	public abstract Transferable getTransferable();
}