import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Commit {
	public enum CommitData {
		PARENT, CHILD, TREE
	};

	private String fileSha1 = "";
	private String tree = "";
	private String nextCommit = "";
	private String parentCommit = "";
	private String summary = "";
	private String author = "";
	private String date = "";
	private boolean log = false;

	public Commit(String summary, String author, String pParentCommit) throws IOException {
		this(summary, author, pParentCommit, false);
	}

	public Commit(String summary, String author, String parentCommit, boolean log) throws IOException {
		this.summary = summary;
		this.author = author;
		this.parentCommit = parentCommit;
		this.log = log;
		createTree();
		setDate();
		generateFileSha1();
		writeCommitAsObject();

		// Link parent to this node if there's a parent
		if (parentCommit != null && !parentCommit.equals("")) {
			this.updateChildCommitInParent(parentCommit, fileSha1);
		}

		Index.resetIndexFile();
	}

	public void createTree() throws IOException {
		Tree t = null;
		if (parentCommit != null && !parentCommit.equals("")) {
			System.out.println("Getting TREE from:" + parentCommit + " : "
					+ Commit.getShaFromCommit("objects/" + parentCommit, Commit.CommitData.TREE));
			t = new Tree(Commit.getShaFromCommit("objects/" + parentCommit, Commit.CommitData.TREE));
		} else {
			t = new Tree();
		}
		tree = t.getFileNameSha1();
	}

	public void writeCommitAsObject() throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(tree).append("\n")
				.append(parentCommit).append("\n")
				.append(nextCommit).append("\n")
				.append(author).append("\n")
				.append(date).append("\n")
				.append(summary).append("\n");

		String fileDataToWrite = stringBuilder.toString();
		String fileToWrite = "objects\\" + getFileSha1();
		if (log) {
			System.out.println("Writing to file:" + fileToWrite);
			System.out.println("Data: " + fileDataToWrite);

		}
		GitUtils.writeToFile(fileToWrite, fileDataToWrite.toString());
	}

	public void updateChildCommitInParent(String commitObjectToUpdate, String locationOfChildSha1) throws IOException {
		String filePath = "objects/" + commitObjectToUpdate;
		String fileText = GitUtils.readFileToString(filePath);
		fileText = GitUtils.replaceLineInString(fileText, 3, locationOfChildSha1);
		GitUtils.writeToFile(filePath, fileText);
	}

	public void generateFileSha1() {
		setFileSha1(GitUtils.encryptThisString(summary + getDate() + author + parentCommit));

		// Log generated SHA1
		if (log == true) {
			System.out.println("Setting SHA1 for commit: objects\\" + fileSha1);
		}
	}

	public void setFileSha1(String sha1String) {
		fileSha1 = sha1String;
	}

	public String getFileSha1() {
		return fileSha1;
	}

	public String getTree() {
		return tree;
	}

	public void setDate() {
		date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		// Log generated date
		if (log == true) {
			System.out.println("Setting date for commit:" + date);
		}
	}

	public String getDate() {
		return date;
	}

	public static String getShaFromCommit(String commit, CommitData commitData) {

		// Check if commit exists
		File commitFileObject = new File(commit);
		if (!commitFileObject.exists()) {
			return null;
		}

		int lineToGrab = 0;
		ArrayList<String> commitDataStrings = GitUtils.readFileToArrayListOfStrings(commit);
		switch (commitData) {
			case PARENT:
				lineToGrab = 1;
				break;
			case CHILD:
				lineToGrab = 2;
				break;
			case TREE:
				lineToGrab = 0;
				break;
			default:
				lineToGrab = 0; // Default is tree
				break;
		}
		return commitDataStrings.get(lineToGrab);
	}
}
