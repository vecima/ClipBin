package clipbin;

import java.awt.Image;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Clip implements Serializable
{
	private static final long serialVersionUID = 1319251539882468369L;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");

	enum ClipType
	{
		STRING, IMAGE, FILE_LIST
	}

	private ClipType fClipType;
	private String fStringData;
	private Image fImageData;
	private List<Path> fPathData;

	private transient Path fClipPath;

	public Clip(String pStringData)
	{
		//System.out.println("clip created from string.");
		this.fClipType = ClipType.STRING;
		this.fStringData = pStringData;
	}

	public Clip(Image pImageData)
	{
		//System.out.println("clip created from image.");
		this.fClipType = ClipType.IMAGE;
		this.fImageData = pImageData;
	}

	public Clip(List<Path> pPathData)
	{
		//System.out.println("clip created from Paths.");
		this.fClipType = ClipType.FILE_LIST;
		this.fPathData = pPathData;
	}

	public boolean save(Path parentPath)
	{
		try {
			Path filePath = parentPath.resolve(dateFormat.format(new Date()) + ".clip");
			ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath, StandardOpenOption.CREATE));
			if (ClipType.IMAGE.equals(this.getClipType())) {
				
				//TODO figure out how to serialize java.awt.Image
				//out.writeObject(this);

			} else if (ClipType.FILE_LIST.equals(this.getClipType())) {
				// doesn't work yet because I think the awt clipboard toolkit really want to use java.io.Files (not nio)
				// maybe I can translate the data somewhere
			} else {
				out.writeObject(this);
			}
			out.close();
			this.setClipPath(filePath);
			return true;
		} catch (IOException x) {
			x.printStackTrace();
			return false;
		}
	}

	public boolean delete()
	{
		try {
			Files.delete(this.fClipPath);
			return true;
		} catch (IOException x) {
			x.printStackTrace();
			return false;
		}
	}

	public String getDisplay(int pMaxCharacters)
	{
		String display = "";
		switch (this.fClipType)
		{
		case IMAGE:
			break;
		case FILE_LIST:
			display = "Files: ";
			if (this.fPathData != null && this.fPathData.size() > 0)
			{
				display += this.fPathData.get(0).getFileName();
				for (int i = 1; i < this.fPathData.size(); i++)
				{
					display += this.fPathData.get(i).getFileName();
					display += ",";
				}
			}
			break;
		case STRING:
			display = this.fStringData;
			break;
		default:
			display = "there was a problem";
			break;
		}
		if (pMaxCharacters != 0 && display.length() > pMaxCharacters)
			display = display.substring(0, Math.min(display.length(), pMaxCharacters)) + "...";
		return display;
	}

	public ClipType getClipType()
	{
		return this.fClipType;
	}

	public String getStringData()
	{
		return this.fStringData;
	}

	public Image getImageData()
	{
		return this.fImageData;
	}

	public List<Path> getPathData()
	{
		return this.fPathData;
	}

	public Path getClipPath()
	{
		return this.fClipPath;
	}

	public void setClipPath(Path pClipPath)
	{
		this.fClipPath = pClipPath;
	}
}