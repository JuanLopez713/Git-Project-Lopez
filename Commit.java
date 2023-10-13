import java.io.IOException;
import java.sql.Date;

public class Commit {
    private static final Blob.Type COMMIT = Blob.Type.COMMIT;
    private String treeSHA;
    private String parentSHA;
    private String author;
    private String summary;

    private Date date;

    private String commitFileName;

    public Commit(String treeSHA, String parentSHA, String author, String summary) {
        this.treeSHA = treeSHA;

        this.parentSHA = parentSHA;
        if (this.parentSHA == null) {
            this.parentSHA = "";
        }

        this.author = author;
        this.summary = summary;
        this.date = new Date(System.currentTimeMillis());
        this.commitFileName = "commit";

    }

    public Commit(String commitSHA) {
        String commitFileContents = GitUtils.readFileToString("objects/" + commitSHA);
        String[] commitFileLines = commitFileContents.split("\n");
        this.treeSHA = commitFileLines[0];
        this.parentSHA = commitFileLines[1];
        this.author = commitFileLines[2];
        this.summary = commitFileLines[3];
        this.date =  new Date(System.currentTimeMillis());
        this.commitFileName = commitSHA;
    }

    // getters

    public String getTreeSHA() {
        return treeSHA;
    }

    public String getParentSHA() {
        return parentSHA;
    }

    public String getAuthor() {
        return author;
    }

    public String getSummary() {
        return summary;
    }

    public String getEntryString() {
        String entry = "";
        entry += treeSHA + "\n";
        entry += parentSHA + "\n";
        entry += author + "\n";
        entry += summary + "\n";
        entry += date;
        return entry;
    }

    // setters

    public void setTreeSHA(String treeSHA) {
        this.treeSHA = treeSHA;
    }

    public void setParentSHA(String parentSHA) {
        this.parentSHA = parentSHA;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    // methods: save, saveCommitFile, makeCommitBlob, deleteCommitFile
    public void save() {
        try {
            saveCommitFile();
            makeCommitBlob();
            deleteCommitFile();
        } catch (IOException e) {
            System.out.println("Error saving commit file");
        }
    }

    public void saveCommitFile() throws IOException {
        System.out.println("Saving commit: " + commitFileName);
        GitUtils.createFile("", commitFileName);
        String entry = getEntryString();
        GitUtils.writeToFile(commitFileName, entry);
    }

    public Blob makeCommitBlob() throws IOException {
        Blob commitBlob = new Blob(commitFileName, COMMIT, true);
        return commitBlob;
    }

    public void deleteCommitFile() {
        GitUtils.deleteFile(commitFileName);
    }

}
