import java.io.IOException;

public class Git {

	public static final String GIT_FOLDER = "git";
	public static final String OBJECTS_FOLDER = "git/objects";
	public static final String HEAD = "HEAD";
	public static final String HEAD_PATH = "git/HEAD";
	public static final String INDEX = "index";
	public static final String INDEX_PATH = "git/index";
	public static Tree tree;

	public Git() {

	}

	public static void init() throws IOException {
		// initialize a Git directories
		GitUtils.createDirectory(GIT_FOLDER);
		GitUtils.createDirectory(OBJECTS_FOLDER);
		// initialize Git files
		GitUtils.createFile(GIT_FOLDER, INDEX);
		GitUtils.createFile(GIT_FOLDER, HEAD);
		// Index.init();
		tree = new Tree();

	}

	// getters
	public static String getFileName(String entry) {
		String[] entryArr = entry.split("\\*");
		String fileName = entryArr[entryArr.length - 1].trim();
		return fileName;
	}

	public static String getEntryType(String entry) {
		String[] entryArr = entry.split("\\*");
		String entryType = entryArr[0].trim();
		return entryType;
	}

	// methods: add, remove, edit

	public static void add(String filePath) throws IOException {
		if (!GitUtils.doesFileExist(filePath)) {
			throw new IOException(
					"The file " + filePath + " you are trying to add does not exist!");
		}
		// Index.add(filePath);
		tree.add(filePath);

	}

	public static void remove(String filePath) throws IOException {
		if (!GitUtils.doesFileExist(filePath)) {
			throw new IOException(
					"The file " + filePath + " you are trying to remove does not exist!");
		}
		// Index.remove(filePath);
		tree.remove(filePath);

	}

	// methods:

	public static void commit(String author, String summary) throws IOException {
		// save Tree blob
		tree.saveTreeFile();
		Blob treeBlob = tree.makeTreeBlob();

		// get parent commit SHA
		String parentCommitSHA = GitUtils.readFileToString(HEAD_PATH);

		// create Commit object
		Commit commit = new Commit(treeBlob.getSHA1(), parentCommitSHA, author, summary);
		commit.saveCommitFile();

		// save Commit Blob
		Blob commitBlob = commit.makeCommitBlob();

		// update HEAD
		GitUtils.writeToFile(HEAD_PATH, commitBlob.getSHA1());

		// clean up
		tree.deleteTreeFile();
		commit.deleteCommitFile();

	}

	public static void checkout(String commitSHA) throws IOException {

		// get commit object
		Commit commit = new Commit(commitSHA);

		// get tree object
		System.out.println("Tree SHA: " + commit.getTreeSHA());
		Tree tree = new Tree(commit.getTreeSHA(), "root");
		tree.restore();
		// update HEAD
		GitUtils.writeToFile(HEAD_PATH, commitSHA);

	}

	// methods: utilities

	public static void createIndexFile() {
		if (!GitUtils.doesFileExist(INDEX_PATH)) {
			GitUtils.createFile(OBJECTS_FOLDER, INDEX);
		}
	}

	public static void reset() throws IOException {
		GitUtils.deleteFile(INDEX_PATH);
		GitUtils.createFile(OBJECTS_FOLDER, INDEX);
	}
}
