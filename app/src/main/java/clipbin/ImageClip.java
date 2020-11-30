package clipbin;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class ImageClip extends Clip
{
	private transient BufferedImage image;
	private String data;

	public ImageClip(Image pImage) {
		//System.out.println("clip created from image.");
		this.type = ClipType.IMAGE;
		this.image = (BufferedImage) pImage;
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			if (ImageIO.write(this.image, "png", byteArrayOutputStream))
				this.data = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
		} catch (IOException x) {
			x.printStackTrace();
			this.data = null;
		}
	}

	@Override
	public Object getData() {
		if (this.image == null && this.data != null) {
			try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(this.data))) {
				this.image = ImageIO.read(byteArrayInputStream);
			} catch (IOException x) {
				x.printStackTrace();
				this.image = null;
			}
		}
		return this.image;
	}

	@Override
	public Transferable getTransferable() {
		return new Transferable() {
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}

			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				return ImageClip.this.getData();
			}
		};
	}

	@Override
	public String getDisplay(int maxCharacters) {
		if (this.display == null)
			this.display = "Image: " + this.data;
		return truncate(this.display, maxCharacters);
	}
}