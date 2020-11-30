package clipbin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ClipFactory implements JsonDeserializer<Clip> {
	public Clip deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		Clip.ClipType type = Clip.ClipType.valueOf(jsonObject.get("type").getAsString());
		if (type == Clip.ClipType.STRING) {
			return context.deserialize(json, StringClip.class);
		} else if (type == Clip.ClipType.IMAGE) {
			return context.deserialize(json, ImageClip.class);
		} else if (type == Clip.ClipType.FILE_LIST) {
			return context.deserialize(json, FileListClip.class);
		}
		throw new JsonParseException("Unrecognized clip type.");
	}

	public static Clip buildFromTransferable(Transferable transferable) {
		if ((transferable != null) && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			try {
				return new ImageClip((Image) transferable.getTransferData(DataFlavor.imageFlavor));
			} catch (UnsupportedFlavorException | IOException ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		} else if ((transferable != null) && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			try {
				return new FileListClip((List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor));
			} catch (UnsupportedFlavorException | IOException ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		} else if ((transferable != null) && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				return new StringClip((String) transferable.getTransferData(DataFlavor.stringFlavor));
			} catch (UnsupportedFlavorException | IOException ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return null;
	}
}