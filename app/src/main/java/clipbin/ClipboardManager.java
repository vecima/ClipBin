package clipbin;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.io.IOException;
import java.util.List;

public final class ClipboardManager
{
	/**
	 * Place a Clip on the Clipboard, and make this class the owner of the Clipboard's contents.
	 */
	public static void setClipboardContents(Clip pClip, ClipboardOwner pClipboardOwner) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(pClip.getTransferable(), pClipboardOwner);
	}

	/**
	 * Get the String residing on the clipboard.
	 *
	 * @return any data found on the Clipboard; if none found, return null.
	 */
	public static Clip getClipboardContents() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// odd: the Object param of getContents is not currently used
		return ClipFactory.buildFromTransferable(clipboard.getContents(null));
	}
}