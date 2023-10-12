import java.io.IOException;
import java.sql.Date;

public class Commit {
    private String treeSHA;
    private String parentSHA;
    private String author;
    private String summary;
    private Date date;

    private String commitFileName;
    private String commitSHA;

    public Commit(String treeSHA, String parentSHA, String author, String summary) throws IOException {
        this.treeSHA = treeSHA;
        this.parentSHA = parentSHA;
        this.author = author;
        this.summary = summary;
        this.date = new Date(System.currentTimeMillis());

        commitFileName = "commit";

        save();

    }

    public void save() throws IOException {
        reset();

        GitUtils.appendToFile(commitFileName, treeSHA);
        if (parentSHA != null) {
            GitUtils.appendToFile(commitFileName, parentSHA);
        } else {
            GitUtils.appendToFile(commitFileName, "");
        }
        GitUtils.appendToFile(commitFileName, author);
        GitUtils.appendToFile(commitFileName, summary);
        GitUtils.appendToFile(commitFileName, date.toString());

        Blob commitBlob = new Blob(commitFileName);
        commitSHA = commitBlob.getSHA1();

    }

    public void reset() {
        GitUtils.deleteFile(commitFileName);

        GitUtils.createFile("", commitFileName);
    }

    public String getSHA1() {
        return commitSHA;
    }

}
