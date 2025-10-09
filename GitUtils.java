import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GitUtils {

	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

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
		// Create nested directories if needed; safe to call if directory exists
		if (folderName == null || folderName.isEmpty()) {
			return;
		}
		try {
			Files.createDirectories(Paths.get(folderName));
		} catch (IOException e) {
			System.out.println("Failed to create directory: " + folderName);
			e.printStackTrace();
		}
	}

	// createFile - creates a file in directory if it does not exist
	public static String createFile(String folderName, String fileName) {
		// Resolve the target path based on provided folder and file names
		Path targetPath;
		if (folderName == null || folderName.isEmpty()) {
			targetPath = Paths.get(fileName);
		} else {
			Path folderPath = Paths.get(folderName);
			try {
				Files.createDirectories(folderPath);
			} catch (IOException e) {
				System.out.println("Failed to create directory: " + folderName);
				e.printStackTrace();
			}
			Path filePath = Paths.get(fileName);
			targetPath = filePath.isAbsolute() ? filePath : folderPath.resolve(filePath);
		}

		ensureParentDirectories(targetPath);

		// Create the file if it does not exist
		if (!Files.exists(targetPath)) {
			try {
				Files.createFile(targetPath);
			} catch (IOException e) {
				System.out.println("Failed to create file: " + targetPath);
				e.printStackTrace();
			}
		}

		return targetPath.toString();
	}

	// writeToFile - writes string content to the file
	// Notes:
	// - Creates parent directories and the file if they do not already exist
	// - Uses UTF-8 to avoid data loss and to align with readFileToString
	// - Propagates IOException to let callers handle failures appropriately
	public static void writeToFile(String fileName, String content) {

		try {
			Path filePath = Paths.get(fileName);

			ensureParentDirectories(filePath);

			// Create or truncate the file and write contents using UTF-8
			Files.writeString(
					filePath,
					content,
					StandardCharsets.UTF_8,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.out.println("Failed to write to file: " + fileName);
			e.printStackTrace();
		}

	}

	// appendToFile - appends string content to the file (creates file if missing)
	// Notes:
	// - Ensures parent directories exist
	// - Uses UTF-8 and appends without truncating existing content
	public static void appendToFile(String fileName, String content) {

		try {
			Path filePath = Paths.get(fileName);

			ensureParentDirectories(filePath);

			Files.writeString(
					filePath,
					content,
					StandardCharsets.UTF_8,
					StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("Failed to append to file: " + fileName);
			e.printStackTrace();
		}
	}

	// writeBytes - writes raw bytes to a file (creates or truncates)
	public static void writeBytes(String fileName, byte[] data) {
		try {
			Path filePath = Paths.get(fileName);
			ensureParentDirectories(filePath);
			Files.write(
					filePath,
					data,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.out.println("Failed to write bytes to file: " + fileName);
			e.printStackTrace();
		}
	}

	// readAllBytes - reads raw bytes from a file
	public static byte[] readAllBytes(String fileName) {
		try {
			return Files.readAllBytes(Paths.get(fileName));
		} catch (IOException e) {
			System.out.println("Failed to read bytes from file: " + fileName);
			e.printStackTrace();
		}
		return null;
	}

	public static String hashFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new IllegalArgumentException("File not found: " + filePath);
		}
		if (file.isDirectory()) {
			throw new IllegalArgumentException("File is a directory: " + filePath);
		}

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			// Stream the file to avoid loading it fully into memory
			try (InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)))) {
				byte[] buffer = new byte[8192];
				int read;
				while ((read = in.read(buffer)) != -1) {
					md.update(buffer, 0, read);
				}
			}

			byte[] messageDigest = md.digest();

			// Convert byte array into signum representation
			StringBuilder hexString = new StringBuilder();
			for (byte b : messageDigest) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String hashContent(String content) {

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
			md.update(bytes);
			byte[] digest = md.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte b : digest) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readFileToString(String fileName) {
		/* Reads entire file contents as a UTF-8 string to match writeToFile */
		String content = "";
		try {
			content = Files.readString(Paths.get(fileName), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static void deleteFile(String file) {

		// Delete a file if it exists; report outcome
		try {
			Path path = Paths.get(file);
			boolean deleted = Files.deleteIfExists(path);
			if (deleted) {
				System.out.println("Deleted the file: " + path.getFileName());
			} else {
				System.out.println("No file to delete: " + path);
			}
		} catch (IOException e) {
			System.out.println("Failed to delete the file: " + file);
			e.printStackTrace();
		}
	}

	public static boolean doesFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	public static String[] getFiles(String filePath) {
		File file = new File(filePath);
		String[] list = file.list();
		return list != null ? list : new String[0];
	}

	public static boolean deleteDirectory(String directoryToBeDeleted) {
		File target = new File(directoryToBeDeleted);
		File[] allContents = target.listFiles();
		boolean success = true;
		if (allContents != null) {
			for (File child : allContents) {
				boolean childDeleted = GitUtils.deleteDirectory(child.getAbsolutePath());
				if (!childDeleted) {
					success = false;
				}
			}
		}
		if (!target.delete()) {
			success = false;
		}
		return success;
	}

	// gets the path of the file in the working directory of the project (not
	// absolute)
	public static String getPath(String fileName) {
		File file = new File(fileName);
		Path path = file.toPath().normalize();
		return path.toString();
	}

	public static Map<String, String> sortByPathDepth(Map<String, String> map) {
		// Convert map entries to a list
		List<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());

		// Precompute depths for each path to avoid repeated counting
		Map<String, Integer> depthByPath = new HashMap<>(list.size());
		for (Map.Entry<String, String> entry : list) {
			String path = entry.getValue();
			depthByPath.put(path, countSlashes(path));
		}

		// Sort the list based on the precomputed depth (descending: deepest first)
		list.sort((entry1, entry2) -> Integer.compare(
				depthByPath.get(entry2.getValue()),
				depthByPath.get(entry1.getValue())));

		// Create a new LinkedHashMap to maintain the order of insertion
		Map<String, String> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	private static int countSlashes(String s) {
		int depth = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '/') {
				depth++;
			}
		}
		return depth;
	}

	public static String[] splitDirectories(String filePath) {

		return filePath.split("/");
	}

	private static void ensureParentDirectories(Path path) {
		Path parent = path.getParent();
		if (parent != null) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e) {
				System.out.println("Failed to create parent directories for: " + path);
				e.printStackTrace();
			}
		}
	}

	public static void reset(String directory) {
		// delete all the git directory
		File directoryFile = new File(directory);
		File[] files = directoryFile.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				reset(file.getPath());
			}
			file.delete();
		}
		directoryFile.delete();
	}

	public static void cleanup(String directory) {
		// delete all the blobs in the objects directory
		GitUtils.reset(directory);
		try {
			Git.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
