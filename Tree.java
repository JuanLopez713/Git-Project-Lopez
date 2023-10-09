import java.io.IOException;
import java.util.ArrayList;

public class Tree {

	private final static String DEFAULT_INDEX_LOCATION = "Tree";
	private String fileNameSha1;
	private ArrayList<String> contentHashMap;

	public Tree(ArrayList<String> treeContent) throws IOException {
		contentHashMap = treeContent;
		this.setFileNameSha1(this.toString());
		GitUtils.writeToFile("objects/" + fileNameSha1, this.toString());
	}

	// No previous tree
	public Tree() throws IOException {
		this(convertIndexToTreeFormat(Tree.DEFAULT_INDEX_LOCATION));
	}

	// With Previous Tree
	
	public Tree(String previousTree) throws IOException {
		contentHashMap = convertIndexToTreeFormat(Tree.DEFAULT_INDEX_LOCATION);
		contentHashMap.add(0, "tree : " + previousTree);
		this.setFileNameSha1(this.toString());
		GitUtils.writeToFile("objects/" + fileNameSha1, this.toString());
	}

	public String toString() {
		String content = "";
		for (int i = 0; i < contentHashMap.size(); i++) {
			content += contentHashMap.get(i);
			if (i != contentHashMap.size() - 1) {
				content += "\n";
			}
		}
		return content;
	}

	public static ArrayList<String> convertIndexToTreeFormat(String indexFileLocation) {

		// Get all additions
		ArrayList<String> allAdditions = new ArrayList<String>();
		allAdditions = getTreeFormattedBlobAdditionsFromIndex(indexFileLocation);
		// Get all additions
		// Get all deleted / modified files
		// Generate a tree from each modified / deleted item

		return allAdditions;

	}

	public void setFileNameSha1(String content) {
		fileNameSha1 = GitUtils.encryptThisString(content);
	}

	public String getFileNameSha1() {
		return fileNameSha1;
	}

	public static ArrayList<String> getIndexListOfRemovedBranches(String indexFileLocation) {
		ArrayList<String> indexData = new ArrayList<String>();
		indexData = GitUtils.readFileToArrayListOfStrings(indexFileLocation);

		ArrayList<String> editedOrRemovedEntries = new ArrayList<String>();
		System.out.println("File location:" + indexFileLocation + " size:" + indexData.size());
		for (int i = 0; i < indexData.size(); i++) {
			String s = indexData.get(i);

			// Check for modified or deleted
			if (s.indexOf("*modified*") != -1 || s.indexOf("*deleted*") != -1) {
				System.out.println(s);
				editedOrRemovedEntries.add(s);
			}

		}
		return editedOrRemovedEntries;
	}

	public static ArrayList<String> getTreeFormattedBlobAdditionsFromIndex(String indexFileLocation) {
		ArrayList<String> indexData = new ArrayList<String>();
		ArrayList<String> treeFormatedIndexData = new ArrayList<String>();
		indexData = GitUtils.readFileToArrayListOfStrings(indexFileLocation);
		for (int i = 0; i < indexData.size(); i++) {
			String s = indexData.get(i);

			// Check for modified or deleted
			if (s.indexOf("*modified*") == -1 && s.indexOf("*deleted*") == -1) {
				String sha1 = s.substring(s.indexOf(" : ") + 3, s.indexOf(" : ") + 43);
				String filePath = s.substring(0, s.indexOf(" : "));
				String formattedBlobAddition = "blob : " + sha1 + " " + filePath;
				treeFormatedIndexData.add(formattedBlobAddition);
			}

		}
		return treeFormatedIndexData;
	}
}
