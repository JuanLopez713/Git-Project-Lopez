import java.io.File;
import java.io.IOException;
import java.util.HashMap;

// Handles writing and removing from index file
// Git class modifies the directory correctly
// Index handles whatever directory its been given
public class Index {
	public static String filePath = "tree";

	public static void add(String fileName, String sha1Hash) throws IOException {
		// Grab file contents and parse
		HashMap<String, String> indexMap = GitUtils.getHashMapFromTextFile(filePath);
		String fileSha1 = indexMap.get(fileName);
		if (fileSha1 == null) {
			indexMap.put(fileName, sha1Hash);

			// Write file back out
			GitUtils.writeHashMapToTextFile(indexMap, filePath);
		}
		return;
	}

	public static void remove(String fileName) throws IOException{
		String indexFullFilePath = filePath;
		System.out.println("File path to hash = " + indexFullFilePath);
		// Grab file contents and parse
		HashMap<String, String> indexMap = GitUtils.getHashMapFromTextFile(indexFullFilePath);
	
		String cleanFileName = fileName;
		System.out.println("File path to hash = " + cleanFileName);
		String fileSha1 = indexMap.get(cleanFileName);
		System.out.println("File path to hash = " + fileSha1);
		if (fileSha1 != null) {

			indexMap.remove(cleanFileName);

			// Write file back out
			GitUtils.writeHashMapToTextFile(indexMap, indexFullFilePath);
			String sha1FileName = "objects/" + fileSha1;

			// Delete file from disk
			File sha1FileToDelete = new File(sha1FileName);
			if (sha1FileToDelete.delete()) {
				System.out.println("Deleted the file: " + sha1FileToDelete.getName());
			} else {
				System.out.println("Failed to delete the file.");
			}
		}
		return;
	}

	public static void resetIndexFile() throws IOException {
		GitUtils.deleteFile(filePath);
		GitUtils.createFile("", filePath);
	}
}
