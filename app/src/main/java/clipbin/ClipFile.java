package clipbin;

import com.google.common.io.Files;

import java.io.File;

public class ClipFile
{
	private transient File file;
	private String filepath;

	public ClipFile(File file) {
		this.file = file;
		initializeFromFile();
	}

	public File getFile() {
		if (this.file == null) {
			System.out.println(String.format("file was null, reading from %s", this.filepath));
			this.file = new File(this.filepath);
		}
		return this.file;
	}

	public String getFilepath() {
		if (this.filepath == null && this.file != null) {
			initializeFromFile();
		}
		return this.filepath;
	}

	public String getFilename() {
		if (this.filepath == null && this.file != null) {
			initializeFromFile();
		}
		return Files.getNameWithoutExtension(this.filepath) + "." + Files.getFileExtension(this.filepath);
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	private void initializeFromFile() {
		this.filepath = this.file.toPath().toString();
	}
}