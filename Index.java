import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Handles writing and removing from index file
// Git class modifies the directory correctly
// Index handles whatever directory its been given
public class Index {
	private static String REFS_FOLDER = "refs";
	private static String INDEX_PATH = "refs/index";


	enum Type {
		ADDED, REMOVED, EDITED
	};

	private static Map<String, Type> indexEntries = new HashMap<String, Type>();

	public static void init() {
		GitUtils.createDirectory(REFS_FOLDER);
		GitUtils.createFile(REFS_FOLDER, INDEX_PATH);
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
		GitUtils.deleteFile(INDEX_PATH);
		GitUtils.createFile(REFS_FOLDER, INDEX_PATH);
		indexEntries = new HashMap<String, Type>();
	}

	public static void updateIndexFile() {
		/* iterates through indexEntries */
		init();
		String entry = "";
		for (Map.Entry<String, Type> indexEntry : indexEntries.entrySet()) {

			// creating entry
			entry += indexEntry.getValue() + "* " + indexEntry.getKey() + "\n";

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
