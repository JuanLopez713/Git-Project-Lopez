
// Blobs store either data or a link to

import java.io.IOException;

public class Blob {
	private String objectFolderPath = "git/objects";
	private String fileSha1;
	private String filePath;
	private String fileContents;

	enum Type {
		BLOB, TREE, COMMIT
	};

	private Type type;

	public Blob() throws IOException {
		this("sample.txt", "sample", Type.BLOB);
	}

	public Blob(String filePath, String fileSHA) throws IOException {
		this(filePath, fileSHA, Type.BLOB);

	}

	public Blob(String filePath, String fileSHA, Type type) throws IOException {

		this.filePath = filePath;
		this.type = type;
		this.fileSha1 = fileSHA;

		// Get fileContents from file
		this.fileContents = GitUtils.readFileToString(filePath);

		if (type == Type.TREE) {
			this.filePath = this.filePath.substring(1);
		}

	}

	// public Blob(String filePath, Type type, boolean save) throws IOException {
	// 	this(filePath, type);
	// 	if (save) {
	// 		save();
	// 	}
	// }

	// public Blob(String[] fileLines) throws IOException {
	// this.type = Type.BLOB;

	// this.fileSha1 = fileLines[1].trim();
	// this.filePath = fileLines[2].trim();
	// this.filePath = objectFolderPath + "/" + fileSha1;
	// this.fileContents = GitUtils.readFileToString(filePath);
	// }

	// Save file to disk
	public void save() throws IOException {
		GitUtils.createDirectory(objectFolderPath);
		this.filePath = GitUtils.createFile(objectFolderPath, fileSha1);
		GitUtils.writeToFile(filePath, fileContents);
	}

	public void restoreFile() throws IOException {
		GitUtils.createDirectory(objectFolderPath);
		this.filePath = GitUtils.createFile(objectFolderPath, filePath);
		GitUtils.writeToFile(filePath, fileContents);
	}

	// getters

	public String getSHA1() {
		return fileSha1;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileContents() {
		return fileContents;
	}
	// setters

	public void setSHA1(String fileSha1) {
		this.fileSha1 = fileSha1;
	}

	public void setFilePath(String fileName) {
		this.filePath = fileName;
	}

	public void setFileContents(String fileContents) {
		this.fileContents = fileContents;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setFolderPath(String folderPath) {
		this.objectFolderPath = folderPath;
	}

	// inherited methods
	public String toString() {
		switch (type) {
			case BLOB:
				return "blob " + fileSha1 + " " + filePath;
			case TREE:
				return "tree " + fileSha1 + " " + filePath;
			case COMMIT:
				return fileSha1;
			default:
				return "blob " + fileSha1 + " " + filePath;
		}

	}

	public void updateHead() throws IOException {
		GitUtils.writeToFile("HEAD", this.getSHA1());
	}
}
