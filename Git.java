import java.io.IOException;
import java.util.ArrayList;

public class Git {
	public Git() {

	}

	public static void init() {
		// initialize a Git directories
		GitUtils.createDirectory("objects");
		GitUtils.createDirectory("refs");

		// initialize Git files
		GitUtils.createFile("", "HEAD");
	}

	public static void add(String fileName) throws IOException {

		String entry = "added* " + fileName;
		createIndexFile();
		GitUtils.appendToFile("index", entry);

	}

	public static void remove(String fileName) throws IOException {
		String entry = "removed* " + fileName;
		createIndexFile();
		GitUtils.appendToFile("index", entry);
	}

	public static void edit(String fileName) throws IOException {
		String entry = "edited* " + fileName;
		createIndexFile();
		GitUtils.appendToFile("index", entry);

	}

	public static void reset() throws IOException {
		GitUtils.deleteFile("index");
		GitUtils.createFile("", "index");
	}

	public static void createIndexFile() {
		if (!GitUtils.doesFileExist("index")) {
			GitUtils.createFile("", "index");
		}
	}

	public static void commit(String author, String summary) throws IOException {
		ArrayList<String> index = GitUtils.readFileToArrayListOfStrings("index");

		Tree tree = new Tree();
		for (String entry : index) {
			if (entry.contains("added*")) {

				String fileName = getFileName(entry);
				System.out.println(fileName);
				if (!GitUtils.doesFileExist(fileName)) {
					throw new IOException("File does not exist");
				}
				if (GitUtils.isDirectory(fileName)) {
					tree.addDirectory(fileName);
				} else {
					tree.addFile(fileName);
				}
			}
			if (entry.contains("edited*")) {
				String fileName = getFileName(entry);
				if (!GitUtils.doesFileExist(fileName)) {
					throw new IOException("File does not exist");
				}
				tree.remove(fileName);
				tree.addFile(fileName);
			}
			if (entry.contains("removed*")) {
				String fileName = getFileName(entry);
				tree.remove(fileName);
				GitUtils.deleteFile(fileName);
			}
		}
		String parentSHA = GitUtils.readFileToString("HEAD");
		Commit commit = new Commit(tree.getSHA1(), parentSHA, author, summary);
		GitUtils.writeToFile("HEAD", commit.getSHA1());
		reset();
		
	}

	public static String getFileName(String entry) {
		String[] entryArr = entry.split("\\*");
		String fileName = entryArr[1].trim();
		return fileName;
	}

	/*
	 * Handles deletion of files from the file system
	 * deleted: src/git/Tree.java
	 */
	public static void checkout(String commitSHA) {

	}

	public static void cleanUp(){
		GitUtils.deleteFile("commit");
		GitUtils.deleteFile("tree");
		GitUtils.deleteFile("index");
	}

}
