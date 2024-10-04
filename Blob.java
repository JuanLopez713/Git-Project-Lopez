
// Blobs store either data or a link to

import java.io.IOException;

public class Blob {

	private String fileSha1;
	private String fileName;
	private String fileContents;
	private String objectDirectoryPath = "git/objects";
	private String filePath;

	enum Type {
		BLOB, TREE, COMMIT
	};

	private Type type;

	public Blob() throws IOException {
		this("sample.txt");
	}

	public Blob(String fileName) throws IOException {
		this(fileName, Type.BLOB);

	}

	public Blob(String fileName, Type type) throws IOException {

		this.fileName = fileName;
		this.type = type;

		// Get fileContents from file
		this.fileContents = GitUtils.readFileToString(fileName);

		if (type == Type.TREE) {
			this.fileName = this.fileName.substring(1);
		}
		this.filePath = objectDirectoryPath + "/" + fileName;

		// Create file hash
		this.fileSha1 = GitUtils.encryptString(fileContents);
	}

	public Blob(String fileName, Type type, boolean save) throws IOException {
		this(fileName, type);
		if (save) {
			save();
		}
	}

	public Blob(String[] fileLines) throws IOException {
		this.type = Type.BLOB;

		this.fileSha1 = fileLines[1].trim();
		this.fileName = fileLines[2].trim();
		this.filePath = objectDirectoryPath + "/" + fileSha1;
		this.fileContents = GitUtils.readFileToString(filePath);
	}

	// Save file to disk
	public void save() throws IOException {
		GitUtils.createDirectory(objectDirectoryPath);
		this.filePath = GitUtils.createFile(objectDirectoryPath, fileSha1);
		GitUtils.writeToFile(filePath, fileContents);
	}


	public void restoreFile() throws IOException {
		GitUtils.createDirectory(objectDirectoryPath);
		this.filePath = GitUtils.createFile(objectDirectoryPath, fileName);
		GitUtils.writeToFile(filePath, fileContents);
	}
	// getters

	public String getSHA1() {
		return fileSha1;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileContents() {
		return fileContents;
	}
	// setters

	public void setSHA1(String fileSha1) {
		this.fileSha1 = fileSha1;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileContents(String fileContents) {
		this.fileContents = fileContents;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setObjectDirectoryPath(String folderPath) {
		this.objectDirectoryPath = folderPath;
	}

	// inherited methods
	public String toString() {
		switch (type) {
			case BLOB:
				return "blob " + fileSha1 + " " + cleanUpFileName();
			case TREE:
				return "tree " + fileSha1 + " " + cleanUpFileName();
			case COMMIT:
				return fileSha1;
			default:
				return "blob " + fileSha1 + " " + cleanUpFileName();
		}

	}

	public String cleanUpFileName() {
		String[] fileNameArray = fileName.split("/");
		return fileNameArray[fileNameArray.length - 1];
	}

	public void updateHead() throws IOException {
		GitUtils.writeToFile("HEAD", this.getSHA1());
	}
}
