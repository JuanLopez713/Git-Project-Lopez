import java.io.IOException;

public class Git {
	public Git() {
		
	}
	
	public static void init() {
		// Check if objects directory exists + create if no or error out
		GitUtils.createDirectory("objects");

		// Save file to disk
		GitUtils.createFile("", "tree");
        
	}

	public static void add(String fileName) throws IOException {
		Blob blob = new Blob(fileName);
		System.out.println(fileName);
		Index.add(fileName, blob.getSha1());
	}
	
	public void remove(String fileName) throws IOException {
		Index.remove(fileName);
	}
	
	/*  Handles deletion of files from the file system
	  deleted:    src/git/Tree.java
	*/
	public void delete(String fileName) {
		
	}

}
