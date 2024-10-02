import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Handles writing and removing from index file
// Git class modifies the directory correctly
// Index handles whatever directory its been given
public class Index {

	private static final String GIT_FOLDER = "git";
	private static final String INDEX = "index";
	private static final String INDEX_PATH = "git/index";

	private static Map<String, String> indexEntries = new HashMap<>();


	public static void add(String filePath) throws IOException {
		addFilesToIndex(filePath); // Recursively process files
		writeToIndexFile(); // Write to index once after all files are added
	}

	public static void addFilesToIndex(String filePath) throws IOException {
		if (GitUtils.isDirectory(filePath)) {
			// get all files in directory
			String[] files = GitUtils.getFiles(filePath);
			for (String file : files) {
				addFilesToIndex(filePath + "/" + file);
			}
		} else {
			// get the hash of the file
			String hash = GitUtils.hashFile(filePath);
			String path = GitUtils.getPath(filePath);
			indexEntries.put(hash, path);
		}
	}

	public static void remove(String filePath) throws IOException {

	}

	public static void resetIndexFile() {
		GitUtils.deleteFile(INDEX);
		GitUtils.createFile(GIT_FOLDER, INDEX);
		indexEntries = new HashMap<>();
	}

	public static void writeToIndexFile() throws IOException {
		/* iterates through indexEntries */
		// Git.init();
		String entry = "";
		indexEntries = GitUtils.sortByPathDepth(indexEntries);
		for (Map.Entry<String, String> indexEntry : indexEntries.entrySet()) {

			// creating entry
			entry += indexEntry.getKey() + " " + indexEntry.getValue() + "\n";

		}
		// entry = entry.substring(0, entry.length() - 1); // remove last newline
		System.out.println("Index Snapshot: \n" + entry);
		try {
			GitUtils.writeToFile(INDEX_PATH, entry);
		} catch (IOException e) {
			System.out.println("Failed to write to index file");
		}

	}

	public static Map<String, String> getIndexEntries() {
		return indexEntries;
	}


}
