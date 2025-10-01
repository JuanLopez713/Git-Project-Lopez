import java.io.IOException;

// Handles writing and removing from index file
// Git class modifies the directory correctly
// Index handles whatever directory its been given
public class Index {

	private static final String GIT_FOLDER = "git";
	private static final String INDEX = "index";
	private static final String INDEX_PATH = "git/index";

	public static void stage(String filePath) throws IOException {
		validateStageInput(filePath);
		// Prepare the path, compute the hash, read the index content
		String normalizedPath = normalizePath(filePath);
		String fileHash = computeFileHash(filePath);
		String indexContent = readIndexContent();

		// If the index is empty, append the entry and create the blob
		if (GitUtils.isEmpty(indexContent)) {
			appendIndexEntry(fileHash, normalizedPath);
			Git.createBlob(fileHash, normalizedPath);
			return;
		}

		// If the index is not empty, build the updated index
		// ResultUpdate is a record that contains a boolean if the entry is found, a boolean if the entry was already up to date, and a String of the updated index content
		ResultUpdate result = buildUpdatedIndex(indexContent, normalizedPath, fileHash);

		// If the entry is up to date, return
		if (result.entryUpToDate()) {
			return;
		}

		// If the entry is found (and not up to date), update the entry
		// Otherwise, append the new entry
		if (result.entryFound()) {
			writeIndexContent(result.updatedIndexContent());
		} else {
			appendIndexEntry(fileHash, normalizedPath);
		}

		// Create the blob
		Git.createBlob(fileHash, normalizedPath);
	}

	public static void unstage(String filePath) throws IOException {
		// Prepare the path, read the index content
		String normalizedPath = normalizePath(filePath);
		String indexContent = readIndexContent();

		// If the index is empty, return
		if (GitUtils.isEmpty(indexContent)) {
			return;
		}

		// Remove the path from the index content, and update the index
		String updated = removePathFromIndex(indexContent, normalizedPath);
		writeIndexContent(updated);
	}

	public static void resetIndexFile() {
		GitUtils.deleteFile(INDEX_PATH);
		GitUtils.createFile(GIT_FOLDER, INDEX);
	}

	// ===== Helper types and methods =====

	private static void validateStageInput(String filePath) throws IOException {
		if (GitUtils.isDirectory(filePath)) {
			throw new IOException("Staging directories is not allowed. Provide a file path.");
		}
		if (!GitUtils.isFile(filePath)) {
			throw new IOException("The file " + filePath + " does not exist or is not a regular file.");
		}
	}

	private static String normalizePath(String filePath) {
		return GitUtils.getPath(filePath);
	}

	private static String computeFileHash(String filePath) {
		return GitUtils.hashFile(filePath);
	}

	private static String readIndexContent() {
		return GitUtils.readFileToString(INDEX_PATH);
	}

	private static void appendIndexEntry(String fileHash, String normalizedPath) throws IOException {
		GitUtils.appendToFile(INDEX_PATH, fileHash + " " + normalizedPath + "\n");
	}

	private static void writeIndexContent(String content) throws IOException {
		GitUtils.writeToFile(INDEX_PATH, content);
	}

	private static record ResultUpdate(boolean entryFound, boolean entryUpToDate, String updatedIndexContent) {
	}

	private static ResultUpdate buildUpdatedIndex(String existingIndexContent, String normalizedPath, String fileHash) {
		// Initialize the result variables
		boolean found = false;
		boolean upToDate = false;
		StringBuilder updatedIndexContent = new StringBuilder();

		// Split the index content into entries
		String[] entries = existingIndexContent.split("\n");
		for (String entry : entries) {
			if (GitUtils.isEmpty(entry)) {
				continue;
			}
			// If the entry is not valid (missing separator),
			// append it to the updated index content
			int separatorIndex = entry.indexOf(' ');
			if (separatorIndex <= 0) {
				updatedIndexContent.append(entry).append('\n');
				continue;
			}
			// Get the entry hash and path
			String entryHash = entry.substring(0, separatorIndex);
			String entryPath = entry.substring(separatorIndex + 1);

			// If the entry path is the same as the normalized path, it has been found
			// and if the entry hash is the same as the file hash, it is up to date
			// so append it to the updated index content, otherwise append the new entry
			if (entryPath.equals(normalizedPath)) {
				found = true;
				if (entryHash.equals(fileHash)) {
					upToDate = true;
					updatedIndexContent.append(entry).append('\n');
				} else {

					updatedIndexContent.append(fileHash).append(' ').append(normalizedPath).append('\n');
				}
			} else {
				// If the entry path is not the same as the normalized path,
				// append it to the updated index content
				updatedIndexContent.append(entry).append('\n');
			}
		}

		return new ResultUpdate(found, upToDate, updatedIndexContent.toString());
	}

	private static String removePathFromIndex(String existingIndexContent, String normalizedPath) {
		// Initialize the result variables
		StringBuilder updatedIndexContent = new StringBuilder();

		// Split the index content into entries
		String[] entries = existingIndexContent.split("\n");
		for (String entry : entries) {
			if (GitUtils.isEmpty(entry)) {
				continue;
			}
			// If the entry is not valid (missing separator),
			// append it to the updated index content
			int separatorIndex = entry.indexOf(' ');
			if (separatorIndex <= 0) {
				updatedIndexContent.append(entry).append('\n');
				continue;
			}

			// If the entry path is not the same as the normalized path,
			// append it to the updated index content
			String entryPath = entry.substring(separatorIndex + 1);
			if (!entryPath.equals(normalizedPath)) {

				updatedIndexContent.append(entry).append('\n');
			}

		}
		return updatedIndexContent.toString();
	}

}
