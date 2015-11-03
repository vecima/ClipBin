package com.codeweasel.clipbin;

import java.awt.Image;
import java.io.File;
import java.io.Serializable;
import java.util.List;

public class Clip implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1319251539882468369L;

	enum ClipType
	{
		STRING, IMAGE, FILE_LIST
	}

	private ClipType fClipType;
	private String fStringData;
	private Image fImageData;
	private List<File> fFileData;

	private transient File fClipFile;

	public Clip(String pStringData)
	{
		this.fClipType = ClipType.STRING;
		this.fStringData = pStringData;
	}

	public Clip(Image pImageData)
	{
		this.fClipType = ClipType.IMAGE;
		this.fImageData = pImageData;
	}

	public Clip(List<File> pFileData)
	{
		this.fClipType = ClipType.FILE_LIST;
		this.fFileData = pFileData;
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
			List<File> fileList = this.fFileData;
			if (fileList != null && fileList.size() > 0)
			{
				display += fileList.get(0).getName();
				for (int i = 1; i < fileList.size(); i++)
				{
					display += fileList.get(i).getName();
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

	public List<File> getFileData()
	{
		return this.fFileData;
	}

	public File getClipFile()
	{
		return this.fClipFile;
	}

	public void setClipFile(File pClipFile)
	{
		this.fClipFile = pClipFile;
	}
}