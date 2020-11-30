package clipbin;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FileListClip extends Clip
{
	private List<ClipFile> data;

	public FileListClip(List<File> pFiles) {
		//System.out.println("clip created from Files.");
		this.type = ClipType.FILE_LIST;
		if (pFiles != null) {
			this.data = new ArrayList<ClipFile>();
			pFiles.forEach(file -> {
				this.data.add(new ClipFile(file));
			});
		}
	}

	@Override
	public Object getData() {
		List<File> fileData = new ArrayList<File>();
		if (this.data != null)
			this.data.forEach(clipFile -> fileData.add(clipFile.getFile()));
		return fileData;
	}

	@Override
	public Transferable getTransferable() {
		return new Transferable() {
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.javaFileListFlavor };
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.javaFileListFlavor.equals(flavor);
			}

			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				return FileListClip.this.getData();
			}
		};
	}

	@Override
	public String getDisplay(int maxCharacters) {
		if (this.display == null) {
			this.display = "Files: ";
			if (this.data != null && this.data.size() > 0) {
				this.display += this.data.get(0).getFilename();
				for (int i = 1; i < this.data.size(); i++) {
					this.display += ", ";
					this.display += this.data.get(i).getFilename();
				}
			}
		}

		return truncate(this.display, maxCharacters);
	}

	@Override
	public boolean save(Path parentPath) {
		Path clipFilePath = assembleFilePath(parentPath);
		Path tempChildPath = parentPath.resolve(com.google.common.io.Files.getNameWithoutExtension(clipFilePath.toString()));
		if (!Files.exists(tempChildPath)) {
			try {
				Files.createDirectories(tempChildPath);
			} catch (IOException x) {
				System.err.format("Couldn't create directory: %s", tempChildPath.toString());
				System.err.format("IOException: %s%n", x);
				return false;
			}
		}

		//copy the files and point the child ClipFile objects at the temp file location.
		try {
			if (this.data != null) {
				/*this.data.forEach(clipFile -> {
					System.out.println(String.format("copying file: %s to: %s", clipFile.getFile().toPath(), tempChildPath));
					Files.copy(clipFile.getFile().toPath(), tempChildPath.resolve(clipFile.getFile().getName()));
					clipFile.setFilepath(clipFile.getFile().toPath().toString());
				});*/
				for (ClipFile clipFile : this.data) {
					System.out.println(String.format("copying file: %s to: %s", clipFile.getFile().toPath(), tempChildPath));
					Path newPath = tempChildPath.resolve(clipFile.getFile().getName());
					Files.copy(clipFile.getFile().toPath(), newPath);
					clipFile.setFilepath(newPath.toString());
				}
			}
		} catch (IOException x) {
			System.err.format("Couldn't copy child file to directory: %s", tempChildPath.toString());
			x.printStackTrace();
			return false;
		}

		return super.save(parentPath);
	}

	@Override
	public boolean delete() {
		try {
			Files.delete(this.getClipPath());
			if (this.data != null) {
				//this.data.forEach(clipFile -> Files.delete(clipFile.getFile().toPath()));
				for (ClipFile clipFile : this.data)
					Files.delete(Path.of(clipFile.getFilepath()));
				Path filesDir = this.getClipPath().getParent().resolve(com.google.common.io.Files.getNameWithoutExtension(this.getClipPath().toString()));
				System.out.println("Deleting files dir: " + filesDir.toString());
				Files.delete(filesDir);
			}
			return true;
		} catch (IOException x) {
			x.printStackTrace();
			return false;
		}
	}
}