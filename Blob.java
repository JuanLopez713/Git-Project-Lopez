
// Blobs store either data or a link to 

import java.io.IOException;

public class Blob {

	private String fileSha1;
	private String fileContents;

	public Blob() throws IOException {
		this("sample.txt");
	}

	public Blob(String fileName) throws IOException {

		GitUtils.createDirectory("objects");

		// Get fileContents from file
		fileContents = GitUtils.readFileToString(fileName);

		// Create file hash
		fileSha1 = GitUtils.encryptString(fileContents);
		// Save file to disk
		String filePath = GitUtils.createFile("objects", fileSha1);
		GitUtils.writeToFile(filePath, fileContents);
	}

	public String getSHA1() {
		return fileSha1;
	}
}
