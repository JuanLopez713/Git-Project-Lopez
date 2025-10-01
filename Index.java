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

		String normalizedPath = normalizePath(filePath);
		String fileHash = computeFileHash(filePath);

		String existing = readIndexContent();
		if (isEmpty(existing)) {
			appendIndexEntry(fileHash, normalizedPath);
			Git.createBlob(fileHash, normalizedPath);
			return;
		}

		UpdateResult result = buildUpdatedIndex(existing, normalizedPath, fileHash);
		if (result.upToDate) {
			return;
		}
		if (result.found) {
			writeIndexContent(result.updatedContent);
		} else {
			appendIndexEntry(fileHash, normalizedPath);
		}
		Git.createBlob(fileHash, normalizedPath);
	}

	public static void unstage(String filePath) throws IOException {
		String normalizedPath = normalizePath(filePath);

		String content = readIndexContent();
		if (isEmpty(content)) {
			return;
		}

		String updated = removePathFromIndex(content, normalizedPath);
		writeIndexContent(updated);
	}

	public static void resetIndexFile() {
		GitUtils.deleteFile(INDEX_PATH);
		GitUtils.createFile(GIT_FOLDER, INDEX);
	}

	// ===== Helper types and methods =====

	private static class UpdateResult {
		final boolean found;
		final boolean upToDate;
		final String updatedContent;

		UpdateResult(boolean found, boolean upToDate, String updatedContent) {
			this.found = found;
			this.upToDate = upToDate;
			this.updatedContent = updatedContent;
		}
	}

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

	private static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	private static void appendIndexEntry(String fileHash, String normalizedPath) throws IOException {
		GitUtils.appendToFile(INDEX_PATH, fileHash + " " + normalizedPath + "\n");
	}

	private static void writeIndexContent(String content) throws IOException {
		GitUtils.writeToFile(INDEX_PATH, content);
	}

	private static UpdateResult buildUpdatedIndex(String existing, String normalizedPath, String fileHash) {
		boolean found = false;
		boolean upToDate = false;
		StringBuilder updated = new StringBuilder();

		String[] lines = existing.split("\n");
		for (String line : lines) {
			if (line == null || line.isEmpty()) {
				continue;
			}
			int sep = line.indexOf(' ');
			if (sep <= 0) {
				updated.append(line).append('\n');
				continue;
			}
			String existingHash = line.substring(0, sep);
			String path = line.substring(sep + 1);
			if (path.equals(normalizedPath)) {
				found = true;
				if (existingHash.equals(fileHash)) {
					upToDate = true;
					// Preserve original line in case we write the file for other reasons
					updated.append(line).append('\n');
				} else {
					updated.append(fileHash).append(' ').append(normalizedPath).append('\n');
				}
			} else {
				updated.append(line).append('\n');
			}
		}

		return new UpdateResult(found, upToDate, updated.toString());
	}

	private static String removePathFromIndex(String content, String normalizedPath) {
		StringBuilder updated = new StringBuilder();
		String[] lines = content.split("\n");
		for (String line : lines) {
			if (line == null || line.isEmpty()) {
				continue;
			}
			int sep = line.indexOf(' ');
			if (sep <= 0) {
				updated.append(line).append('\n');
				continue;
			}
			String path = line.substring(sep + 1);
			if (!path.equals(normalizedPath)) {
				updated.append(line).append('\n');
			}
		}
		return updated.toString();
	}

}
