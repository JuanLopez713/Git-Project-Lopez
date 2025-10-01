import java.io.IOException;

public class Git {

	public static final String GIT_DIRECTORY = "git";
	public static final String OBJECTS_DIRECTORY = "git/objects";
	public static final String HEAD = "HEAD";
	public static final String HEAD_PATH = "git/HEAD";
	public static final String INDEX = "index";
	public static final String INDEX_PATH = "git/index";
	public static final String TEMPFILE = "temp";

	// initialize Git repository
	public static void init() throws IOException {
		// check if Git repository already exists
		if (GitUtils.doesFileExist(GIT_DIRECTORY) && GitUtils.doesFileExist(OBJECTS_DIRECTORY)
				&& GitUtils.doesFileExist(HEAD_PATH) && GitUtils.doesFileExist(INDEX_PATH)) {
			System.out.println("Git repository already exists!");
			return;
		}

		// initialize a Git directories
		GitUtils.createDirectory(GIT_DIRECTORY);
		GitUtils.createDirectory(OBJECTS_DIRECTORY);

		// initialize Git files
		GitUtils.createFile(GIT_DIRECTORY, HEAD);
		GitUtils.createFile(GIT_DIRECTORY, INDEX);
		GitUtils.createFile(GIT_DIRECTORY, TEMPFILE);

	}

	// Save file to disk
	public static void createBlob(String fileSha, String filePath) throws IOException {
		GitUtils.createDirectory(OBJECTS_DIRECTORY);
		String objectPath = OBJECTS_DIRECTORY + "/" + fileSha;
		if (GitUtils.doesFileExist(objectPath)) {
			return; // blob already exists
		}
		GitUtils.createFile(OBJECTS_DIRECTORY, fileSha);

		byte[] data = GitUtils.readAllBytes(filePath);
		GitUtils.writeBytes(objectPath, data);
	}

	// add file entry to index

	public static void add(String fileName) throws IOException {
		if (!GitUtils.doesFileExist(fileName)) {
			throw new IOException("The file " + fileName + " you are trying to add does not exist!");
		}
		Index.stage(fileName);
	}

	// remove file entry from index
	public static void remove(String filePath) throws IOException {
		if (!GitUtils.doesFileExist(filePath)) {
			throw new IOException(
					"The file " + filePath + " you are trying to remove does not exist!");
		}
		Index.unstage(filePath); // to-do: implement remove method
	}

	// to-do: implement commit method
	public static void commit(String author, String summary) throws IOException {

	}

	// to-do: implement checkout method
	public static void checkout(String commitSHA) throws IOException {

		// get latest commit object sha

		// update HEAD
		GitUtils.writeToFile(HEAD_PATH, commitSHA);

	}

	// methods: utilities

	public static void createIndexFile() {
		if (!GitUtils.doesFileExist(INDEX_PATH)) {
			GitUtils.createFile(GIT_DIRECTORY, INDEX);
		}
	}

	public static void reset() throws IOException {
		GitUtils.deleteFile(INDEX_PATH);
		GitUtils.createFile(GIT_DIRECTORY, INDEX);
	}

}
