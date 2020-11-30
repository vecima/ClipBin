package clipbin;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class StringClip extends Clip
{
	private String data;

	public StringClip(String stringData) {
		//System.out.println("clip created from string.");
		this.type = ClipType.STRING;
		this.data = stringData;
	}

	@Override
	public Object getData() {
		return this.data;
	}

	@Override
	public Transferable getTransferable() {
		return new StringSelection(this.data);
	}

	@Override
	public String getDisplay(int maxCharacters) {
		if (this.display == null)
			this.display = this.data;
		return truncate(this.display, maxCharacters);
	}
}