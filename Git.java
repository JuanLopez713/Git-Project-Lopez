import java.io.IOException;
import java.util.Date;

public class Git {

	public static final String GIT_DIRECTORY = "git";
	public static final String OBJECTS_DIRECTORY = "git/objects";
	public static final String HEAD = "HEAD";
	public static final String HEAD_PATH = "git/HEAD";
	public static final String INDEX = "index";
	public static final String INDEX_PATH = "git/index";
	public static final String WORKING_LIST_FIlE = "working-list";

	// initialize Git repository
	public static void init() {
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
		GitUtils.createFile(GIT_DIRECTORY, WORKING_LIST_FIlE);

	}

	// Save file to disk
	public static void createBlob(String fileSha, String filePath) {
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

	public static void add(String fileName) {
		try {
			if (!GitUtils.doesFileExist(fileName)) {
				throw new IOException("The file " + fileName + " you are trying to add does not exist!");
			}
			Index.stage(fileName);
		} catch (IOException e) {
			System.out.println("Failed to add file: " + fileName);
			e.printStackTrace();
		}
	}

	// remove file entry from index
	public static void remove(String filePath) {
		try {
			if (!GitUtils.doesFileExist(filePath)) {
				throw new IOException(
						"The file " + filePath + " you are trying to remove does not exist!");
			}
			Index.unstage(filePath); // to-do: implement remove method
		} catch (IOException e) {
			System.out.println("Failed to remove file: " + filePath);
			e.printStackTrace();
		}
	}

	// to-do: implement commit method
	public static String commit(String author, String summary) {
		try {
			Tree.initializeWorkingListFile();
			Tree.buildTreesFromWorkingList();
			String rootTreeSha = Tree.getRootTreeSha();

			StringBuilder commitContent = new StringBuilder();
			commitContent.append("tree: ").append(rootTreeSha).append("\n");
			commitContent.append("parent: ").append(GitUtils.readFileToString(HEAD_PATH)).append("\n");
			commitContent.append("author: ").append(author).append("\n");
			commitContent.append("date: ").append(new Date().toString()).append("\n");
			commitContent.append("summary: ").append(summary).append("\n");
			commitContent.append("\n");

			String commitSha = GitUtils.hashContent(commitContent.toString());
			GitUtils.writeToFile(HEAD_PATH, commitSha);
			GitUtils.createFile(OBJECTS_DIRECTORY, commitSha);
			GitUtils.writeToFile(OBJECTS_DIRECTORY + "/" + commitSha, commitContent.toString());
			return commitSha;
		} catch (IOException e) {
			System.out.println("Failed to commit: " + author + " " + summary);
			e.printStackTrace();
		}
		return "";
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
