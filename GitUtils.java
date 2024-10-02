import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GitUtils {

	public static boolean isFile(String fileName) {
		File file = new File(fileName);
		return file.isFile();
	}

	public static boolean isDirectory(String fileName) {
		File file = new File(fileName);
		return file.isDirectory();
	}

	// createDirectory - creates a directory if it does not exist
	public static void createDirectory(String folderName) {
		File file = new File(folderName);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	// createFile - creates a file in directory if it does not exist
	public static String createFile(String folderName, String fileName) {
		createDirectory(folderName);

		if (!fileName.startsWith(folderName)) {
			fileName = folderName + "/" + fileName;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Failed to create file: " + fileName);
				e.printStackTrace();
			}
		}

		return fileName;
	}

	// writeToFile - writes the contents to the file
	public static void writeToFile(String fileName, String content) throws IOException {

		File file = new File(fileName);
		if (!file.exists()) {
			throw new IOException("File not found: " + fileName);
		}

		// write the contents to the file
		Path filePath = Paths.get(fileName);
		try {
			Files.writeString(filePath, content, StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String hashFile(String input) {
		File file = new File(input);
		if (!file.exists()) {
			throw new IllegalArgumentException("File not found: " + input);
		}
		if (file.isDirectory()) {
			throw new IllegalArgumentException("File is a directory: " + input);
		}

		// get file contents
		String content = readFileToString(input);

		try {
			// Create MessageDigest instance for SHA-1
			MessageDigest md = MessageDigest.getInstance("SHA-1");

			// Add input bytes to digest
			byte[] messageDigest = md.digest(content.getBytes());

			// Convert byte array into signum representation
			StringBuilder hexString = new StringBuilder();
			for (byte b : messageDigest) {
				// Convert each byte to hex
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			// Output the SHA-1 hash
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readFileToString(String fileName) {
		/* This method finds the file and reads the contents and returns a string */
		String content = "";
		try {
			content = Files.readString(Paths.get(fileName), StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static void printStringArray(String[] array) {
		for (String string : array) {
			System.out.println(string);
		}
	}

	public static void deleteFile(String file) {

		String sha1FileName = file;

		// Delete file from disk
		File fileToDelete = new File(sha1FileName);
		if (fileToDelete.delete()) {
			System.out.println("Deleted the file: " + fileToDelete.getName());
		} else {
			System.out.println("Failed to delete the file.");
		}
	}

	public static boolean doesFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	public static String[] getFiles(String filePath) {
		File file = new File(filePath);
		return file.list();
	}


	public static boolean deleteDirectory(String directoryToBeDeleted) {
		File directoryToDelete = new File(directoryToBeDeleted);
		File[] allContents = directoryToDelete.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				GitUtils.deleteDirectory(file.getAbsolutePath());
			}
		}
		return directoryToDelete.delete();
	}

	// gets the path of the file in the working directory of the project (not absolute)
	public static String getPath(String fileName) {
		File file = new File(fileName);
		Path path = file.toPath();
		return path.toString();
	}

	public static Map<String, String> sortByPathDepth(Map<String, String> map) {
		// Convert map entries to a list
		List<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());

		// Sort the list based on the depth of the file path
		list.sort((entry1, entry2) -> {
			// Calculate the depth of each path by counting the number of "/"
			int depth1 = entry1.getValue().split("/").length;
			int depth2 = entry2.getValue().split("/").length;

			// Sort in descending order (deepest path first)
			return Integer.compare(depth2, depth1);
		});

		// Create a new LinkedHashMap to maintain the order of insertion
		Map<String, String> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static String[] splitDirectories(String filePath) {
		
		return filePath.split("/");
	}

}
