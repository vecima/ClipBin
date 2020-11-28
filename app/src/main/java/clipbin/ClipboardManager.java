package clipbin;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class ClipboardManager
{
	/**
	 * Place a Clip on the Clipboard, and make this class the owner of the Clipboard's contents.
	 */
	public static void setClipboardContents(Clip pClip, ClipboardOwner pClipboardOwner)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		switch (pClip.getClipType())
		{
		case IMAGE:
			break;
		case FILE_LIST:
			FileListTransferable fileListTransferable = new FileListTransferable(pClip.getPathData());
			clipboard.setContents(fileListTransferable, pClipboardOwner);
			break;
		case STRING:
			StringSelection stringSelection = new StringSelection(pClip.getStringData());
			clipboard.setContents(stringSelection, pClipboardOwner);
			break;
		default:
			break;
		}
	}

	/**
	 * Get the String residing on the clipboard.
	 *
	 * @return any text found on the Clipboard; if none found, return an empty String.
	 */
	public static Clip getClipboardContents()
	{
		Clip result = null;
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		// odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.imageFlavor))
		{
			try
			{
				result = new Clip((Image) contents.getTransferData(DataFlavor.imageFlavor));
			}
			catch (UnsupportedFlavorException | IOException ex)
			{
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		else if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
		{
			try
			{
				result = new Clip((List<Path>) contents.getTransferData(DataFlavor.javaFileListFlavor));
			}
			catch (UnsupportedFlavorException | IOException ex)
			{
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		else if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
		{
			try
			{
				result = new Clip((String) contents.getTransferData(DataFlavor.stringFlavor));
			}
			catch (UnsupportedFlavorException | IOException ex)
			{
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return result;
	}
}

class ImageTransferable implements Transferable
{
	private final Image fImage;

	public ImageTransferable(Image pImage)
	{
		this.fImage = pImage;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return DataFlavor.imageFlavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		return this.fImage;
	}
}

class FileListTransferable implements Transferable
{
	private final List<Path> fPaths;

	public FileListTransferable(List<Path> pPaths)
	{
		this.fPaths = pPaths;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { DataFlavor.javaFileListFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return DataFlavor.javaFileListFlavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		return this.fPaths;
	}
}