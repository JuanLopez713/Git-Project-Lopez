import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GitUtils {

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

	public static String encryptString(String input) {
		try {
			// getInstance() method is called with algorithm SHA-1
			MessageDigest md = MessageDigest.getInstance("SHA-1");

			// digest() method is called
			// to calculate message digest of the input string
			// returned as array of byte
			byte[] messageDigest = md.digest(input.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);

			// Add preceding 0s to make it 32 bit
			while (hashtext.length() < 40) {
				hashtext = "0" + hashtext;
			}

			// return the HashText
			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
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

	public static boolean isDirectory(String fileName) {
		File file = new File(fileName);
		return file.isDirectory();
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

}
