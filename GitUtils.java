import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

public class GitUtils {

	public static void createDirectory(String folderName){
		File file = new File(folderName);
		if (!file.exists()){
			file.mkdir();
		}
	}

	public static String createFile(String folderName, String fileName){
		createDirectory(folderName);
		if (!fileName.startsWith(folderName)){
			fileName = folderName + "/" + fileName;
		}
		File file = new File(fileName);
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Failed to create file: " + fileName);
				e.printStackTrace();
			}
		}
		return fileName;
	}

	public static void writeToFile(String fileName, String content) throws IOException{

		File file = new File(fileName);
		if(!file.exists()){
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

	public static String encryptThisString(String input) {
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
			while (hashtext.length() < 32) {
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

	public static String zipCompress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toString("ISO-8859-1");
	}

	public static void writeHashMapToTextFile(HashMap<String, String> mapToWrite, String fileName) {
		String stringToWrite = "";
		// iterate over HashMap entries
		for (Map.Entry<String, String> entry : mapToWrite.entrySet()) {
			stringToWrite += entry.getKey() + " : " + entry.getValue() + '\n';
		}

		// Save file to disk
		Path path = Paths.get(fileName);
		try {
			Files.writeString(path, stringToWrite, StandardCharsets.ISO_8859_1);
		} catch (IOException exception) {
			System.out.println("Write failed for " + fileName);
		}
	}

	public static String hashMapToString(HashMap<String, String> mapToWrite) {
		String stringToWrite = "";
		// iterate over HashMap entries
		for (Map.Entry<String, String> entry : mapToWrite.entrySet()) {
			stringToWrite += entry.getKey() + " : " + entry.getValue() + '\n';
		}
		return stringToWrite;
	}

	public static HashMap<String, String> getHashMapFromTextFile(String filePath) {

		HashMap<String, String> map = new HashMap<String, String>();
		BufferedReader br = null;

		try {

			// create file object
			File file = new File(filePath);

			// create BufferedReader object from the File
			br = new BufferedReader(new FileReader(file));

			String line = null;

			// read file line by line
			while ((line = br.readLine()) != null) {

				// split the line by :
				String[] parts = line.split(":");

				// first part is name, second is number
				String name = parts[0].trim();
				String number = parts[1].trim();

				// put name, number in HashMap if they are
				// not empty
				if (!name.equals("") && !number.equals(""))
					map.put(name, number);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// Always close the BufferedReader
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
				}
				;
			}
		}

		return map;
	}

	public static String readFileToString(String fileName) {
		Path filePath = Paths.get(fileName);
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(filePath, StandardCharsets.ISO_8859_1)) {
			// Read the content with Stream
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String content = contentBuilder.toString();

		// Check if the content ends with a newline character
		if (!content.isEmpty() && content.charAt(content.length() - 1) == '\n') {
			// Remove the last newline character
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

	public static ArrayList<String> readFileToArrayListOfStrings(String fileName) {
		Path filePath = Paths.get(fileName);
		ArrayList<String> listOfStrings = new ArrayList<String>();
		try (Stream<String> stream = Files.lines(filePath, StandardCharsets.ISO_8859_1)) {
			// Read the content with Stream
			stream.forEach(s -> listOfStrings.add(s));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return listOfStrings;
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

	public static String replaceLineInString(String stringToFindAndReplace, int lineNumber, String replacementLine) {
		StringBuffer str = new StringBuffer(stringToFindAndReplace);
		int currentLine = 0;
		int positionOfLastNewLine = 0;
		for (int currentPosition = str.indexOf("\n"); currentPosition != -1; currentPosition = str.indexOf("\n",
				currentPosition + 1)) {
			currentLine++;
			// System.out.println("\\n at " + currentPosition);
			if (currentLine == lineNumber) {
				// System.out.println("Replaceing newline between position:" +
				// positionOfLastNewLine + " and " + currentPosition + " with:"+
				// replacementLine);
				str.replace(positionOfLastNewLine, currentPosition, "\n" + replacementLine);
			}
			positionOfLastNewLine = currentPosition;
		}
		return str.toString();
	}
}
