import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Handles writing and removing from index file
// Git class modifies the directory correctly
// Index handles whatever directory its been given
public class Index {
	private static String GIT_FOLDER = "git/refs";
	private static String INDEX = "index";
	private static String INDEX_PATH = "git/index";



	enum Type {
		ADDED, REMOVED, EDITED
	};

	private static Map<String, Type> indexEntries = new HashMap<String, Type>();

	public static void init() {
		GitUtils.createDirectory(GIT_FOLDER);
		GitUtils.createFile(GIT_FOLDER, INDEX);
	}

	public static void add(String filePath) throws IOException {
		indexEntries.put(filePath, Type.ADDED);
		updateIndexFile();
	}

	public static void remove(String filePath) throws IOException {
		indexEntries.put(filePath, Type.REMOVED);
		updateIndexFile();
	}

	public static void edit(String filePath) throws IOException {
		indexEntries.put(filePath, Type.EDITED);
		updateIndexFile();
	}

	public static void resetIndexFile() throws IOException {
		GitUtils.deleteFile(INDEX);
		GitUtils.createFile(GIT_FOLDER, INDEX);
		indexEntries = new HashMap<String, Type>();
	}

	public static void updateIndexFile() throws IOException {
		/* iterates through indexEntries */
		Git.init();
		String entry = "";
		for (Map.Entry<String, Type> indexEntry : indexEntries.entrySet()) {

			// creating entry
			entry += indexEntry.getValue() + " : blob : " + indexEntry.getKey() + "\n";

		}
		entry = entry.substring(0, entry.length() - 1);
		System.out.println("Entry : " + entry);
		try {
			GitUtils.writeToFile(INDEX_PATH, entry);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void commitInstructions() {
		for (Map.Entry<String, Type> indexEntry : indexEntries.entrySet()) {

			switch (indexEntry.getValue()) {
				case ADDED:

					break;

				case REMOVED:

					break;

				case EDITED:

					break;

				default:
					break;
			}
		}
	}

}
