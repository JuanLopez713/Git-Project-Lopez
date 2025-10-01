
// Blobs store either data or a link to

import java.io.IOException;

public class Blob {
<<<<<<< HEAD
	private String objectFolderPath = "git/objects";
	private String fileSha;
=======

	private String fileSha1;
	private String fileName;
	private String fileContents;
	private String objectDirectoryPath = "git/objects";
>>>>>>> updating-index
	private String filePath;
	private String fileContents;
	private Type type;

	public enum Type {
		BLOB, TREE, COMMIT
	}

	// constructors
	public Blob() throws IOException {
		this.fileSha = "sampleSHA1";
		this.filePath = "samplePath";
		this.fileContents = "sampleContents";
	}

	public Blob(String fileSha, String filePath) throws IOException {

		this.fileSha = fileSha;
		this.filePath = filePath;

		// Get fileContents from file
		this.fileContents = GitUtils.readFileToString(filePath);

<<<<<<< HEAD
		this.type = Type.BLOB;

=======
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
>>>>>>> updating-index
	}

	// Save file to disk
	public void save() throws IOException {
<<<<<<< HEAD
		GitUtils.createDirectory(objectFolderPath);
		this.filePath = GitUtils.createFile(objectFolderPath, fileSha);
=======
		GitUtils.createDirectory(objectDirectoryPath);
		this.filePath = GitUtils.createFile(objectDirectoryPath, fileSha1);
>>>>>>> updating-index
		GitUtils.writeToFile(filePath, fileContents);
	}

	public void restoreFile() throws IOException {
<<<<<<< HEAD
		GitUtils.createDirectory(objectFolderPath);
		this.filePath = GitUtils.createFile(objectFolderPath, filePath);
=======
		GitUtils.createDirectory(objectDirectoryPath);
		this.filePath = GitUtils.createFile(objectDirectoryPath, fileName);
>>>>>>> updating-index
		GitUtils.writeToFile(filePath, fileContents);
	}

	// getters

	public String getSHA1() {
		return fileSha;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileContents() {
		return fileContents;
	}
	// setters

	public void setSHA1(String fileSha) {
		this.fileSha = fileSha;
	}

	public void setFilePath(String fileName) {
		this.filePath = fileName;
	}

	public void setFileContents(String fileContents) {
		this.fileContents = fileContents;
	}

<<<<<<< HEAD
	public void setFolderPath(String folderPath) {
		this.objectFolderPath = folderPath;
=======
	public void setType(Type type) {
		this.type = type;
	}

	public void setObjectDirectoryPath(String folderPath) {
		this.objectDirectoryPath = folderPath;
>>>>>>> updating-index
	}

	// inherited methods
	public String toString() {
<<<<<<< HEAD
=======
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
>>>>>>> updating-index

		return fileSha + " " + filePath;

	}

	public void updateHead() throws IOException {
		GitUtils.writeToFile("HEAD", this.getSHA1());
	}

	public static void createBlob(String filePath) throws IOException {

		if (GitUtils.isDirectory(filePath)) {
			Tree tree = new Tree(filePath);
			String[] files = GitUtils.getFiles(filePath);
			for (String file : files) {
				String relativePath = filePath + "/" + file;
				String fileHash = GitUtils.hashFile(relativePath);
				Blob blobFile = new Blob(fileHash, relativePath);
				tree.add(blobFile.getSHA1(), relativePath);

			}
			tree.save();

		} else {
			String fileHash = GitUtils.hashFile(filePath);
			Blob blob = new Blob(fileHash, filePath);
			blob.save();

		}

	}
}
