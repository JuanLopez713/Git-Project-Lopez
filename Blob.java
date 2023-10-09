import java.io.IOException;

// Blobs store either data or a link to 
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
		fileSha1 = GitUtils.encryptThisString(fileContents);

		// Save file to disk
		String filePath = GitUtils.createFile("objects", fileSha1);
		GitUtils.writeToFile(filePath, fileContents);
	}

	public String getSha1() {
		return fileSha1;
	}
}
