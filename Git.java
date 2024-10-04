import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Git {

	public static final String GIT_FOLDER = "git";
	public static final String OBJECTS_FOLDER = "git/objects";
	public static final String REFS_FOLDER = "git/refs";
	public static final String HEAD = "HEAD";
	public static final String HEAD_PATH = "git/HEAD";
	public static final String INDEX = "index";
	public static final String INDEX_PATH = "git/index";

	public static void init() throws IOException {
		// check if Git repository already exists
		if (GitUtils.doesFileExist(GIT_FOLDER) && GitUtils.doesFileExist(OBJECTS_FOLDER)
				&& GitUtils.doesFileExist(HEAD_PATH) && GitUtils.doesFileExist(INDEX_PATH)) {
			System.out.println("Git repository already exists!");
			return;
		}

		// initialize a Git directories
		GitUtils.createDirectory(GIT_FOLDER);
		GitUtils.createDirectory(OBJECTS_FOLDER);


		// initialize Git files
		GitUtils.createFile(GIT_FOLDER, HEAD);
		GitUtils.createFile(GIT_FOLDER, INDEX);

	}



	// methods: add and remove

	public static void add(String path) throws IOException {
		if (!GitUtils.doesFileExist(path)) {
			throw new IOException("The file " + path + " you are trying to add does not exist!");
		}
		Index.add(path);
		Blob.createBlob(path);

	}

	public static void remove(String filePath) throws IOException {
		if (!GitUtils.doesFileExist(filePath)) {
			throw new IOException(
					"The file " + filePath + " you are trying to remove does not exist!");
		}
		Index.remove(filePath); // to-do: implement remove method
	}

	// to-do: implement commit method
	public static void commit(String author, String summary) throws IOException {
		File file = new File(INDEX_PATH);
		if (!file.exists()) {
			throw new IOException(
					"Index file does not exist. Please add files to index before committing.");
		}


		// Index.getIndexEntries();

		// return the contents of the index file as an array of strings for every new line
		String[] indexContents = GitUtils.readFileToString(INDEX_PATH).split("\n");
		// GitUtils.printStringArray(indexContents);

		// create a blob object for each file in the index
		Tree root = new Tree();
		for (String indexEntry : indexContents) {
			
			String[] entry = indexEntry.split(" ");
			String fileSHA = entry[0];
			String filePath = entry[1];
			int length = GitUtils.splitDirectories(filePath).length;
			String currentFolder = GitUtils.splitDirectories(filePath)[length-2];
			System.out.println("Current Folder: " + currentFolder);
			// Blob blob = new Blob(filePath, fileSHA);
			// blob.save();
			

		}

		// create a tree object for the blobs



	}

	// to-do: implement checkout method
	public static void checkout(String commitSHA) throws IOException {

		// get latest commit object sha

		// update HEAD
		// GitUtils.writeToFile(HEAD_PATH, commitSHA);

	}

	// methods: utilities

	public static void createIndexFile() {
		if (!GitUtils.doesFileExist(INDEX_PATH)) {
			GitUtils.createFile(REFS_FOLDER, INDEX);
		}
	}

	public static void reset() throws IOException {
		GitUtils.deleteFile(INDEX_PATH);
		GitUtils.createFile(REFS_FOLDER, INDEX);
	}
}
