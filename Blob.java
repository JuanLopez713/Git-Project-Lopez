
// Blobs store either data or a link to

import java.io.IOException;

public class Blob {
	private String objectFolderPath = "git/objects";
	private String fileSha;
	private String filePath;
	private String fileContents;

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

	}

	// Save file to disk
	public void save() throws IOException {
		GitUtils.createDirectory(objectFolderPath);
		this.filePath = GitUtils.createFile(objectFolderPath, fileSha);
		GitUtils.writeToFile(filePath, fileContents);
	}

	public void restoreFile() throws IOException {
		GitUtils.createDirectory(objectFolderPath);
		this.filePath = GitUtils.createFile(objectFolderPath, filePath);
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

	public void setFolderPath(String folderPath) {
		this.objectFolderPath = folderPath;
	}

	// inherited methods
	public String toString() {

		return fileSha + " " + filePath;

	}

	public void updateHead() throws IOException {
		GitUtils.writeToFile("HEAD", this.getSHA1());
	}
}
