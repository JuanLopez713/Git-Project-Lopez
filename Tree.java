import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Tree {

    private static final Blob.Type TREE = Blob.Type.TREE;

    private String treeFileName;
    private String treeFileContents;

    private ArrayList<Blob> treeEntries;

    private String treeFilePath = "";
    private String folderPath = "";

    public Tree() throws IOException {

        treeEntries = new ArrayList<Blob>();
        this.treeFileName = "tree";
        System.out.println("Creating tree: " + treeFileName);

    }

    public Tree(String treeFileName) throws IOException {
        treeEntries = new ArrayList<Blob>();
        this.treeFileName = "t" + treeFileName;

    }

    // constructor for restoring TREE files
    public Tree(String treeSHA, String folderName) throws IOException {
        this.treeFileName = treeSHA.trim();
        this.treeFilePath = "objects/" + treeSHA;

        this.treeEntries = new ArrayList<Blob>();
        this.folderPath = folderName;


    }

    public void restore() throws IOException {
        System.out.println("Rebuilding Folder: " + folderPath);
        this.treeFileContents = GitUtils.readFileToString(treeFilePath);
        String[] treeFileLines = treeFileContents.split("\n");

        GitUtils.createDirectory(folderPath);

        for (String line : treeFileLines) {

            String[] lineParts = line.split(":");

            if (line.startsWith("tree")) {
                String subFolder = folderPath + "/" + lineParts[2].trim();
                Tree childTree = new Tree(lineParts[1].trim(), subFolder);
                childTree.restore();

            } else {
                Blob blob = new Blob(lineParts);
                blob.setObjectDirectoryPath(folderPath);
                blob.restoreFile();
                treeEntries.add(blob);

            }

        }
    }

    // method -> add
    public void add(String fileName) throws IOException {

        if (!GitUtils.doesFileExist(fileName)) {
            throw new IOException("File does not exist");
        }

        if (GitUtils.isDirectory(fileName)) {
            addDirectory(fileName);
            return;
        }

        addFile(fileName);

    }

    // method -> addFile
    public void addFile(String fileName) throws IOException {

        if (!GitUtils.doesFileExist(fileName)) {
            throw new IOException("File does not exist");
        }

        Blob blob = new Blob(fileName, Blob.Type.BLOB, true);
        treeEntries.add(blob);
    }

    // method -> addDirectory
    public void addDirectory(String folderName) throws IOException {
        File directory = new File(folderName);

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }

        System.out.println("Adding directory: " + folderName);

        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {

                // build the sub tree and save file
                String subTreeFileName = file.getName();
                String subDirectory = folderName + "/" + subTreeFileName;
                subTreeFileName = file.getName();

                Tree subTree = new Tree(subTreeFileName);
                subTree.addDirectory(subDirectory);
                subTree.saveTreeFile();

                // make a blob of the sub tree
                // add blob to the treeEntries
                // delete the sub tree file
                Blob blob = subTree.makeTreeBlob();
                treeEntries.add(blob);
                subTree.deleteTreeFile();

            } else {
                addFile(folderName + "/" + file.getName());
            }
        }

    }

    // method -> remove
    public void remove(String fileName) {
        if (!GitUtils.doesFileExist(fileName)) {
            throw new IllegalArgumentException("File does not exist");
        }

        removeBlob(fileName);

    }

    // method ->
    public void removeBlob(String fileName) {
        for (Blob blob : treeEntries) {
            if (blob.getFileName().equals(fileName)) {
                treeEntries.remove(blob);
                return;
            }
        }
    }

    // methods: save, saveTreeFile, makeTreeBlob, deleteTreeFile

    public void save() {
        try {
            saveTreeFile();
            makeTreeBlob();
            deleteTreeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreFiles() throws IOException {
        for (Blob blob : treeEntries) {

            blob.restoreFile();
        }
    }

    public void saveTreeFile() throws IOException {
        System.out.println("Saving tree: " + treeFileName);
        GitUtils.createFile("", treeFileName);
        String entry = "";
        for (Blob blob : treeEntries) {
            entry += blob.toString() + "\n";
        }
        entry = entry.substring(0, entry.length() - 1);
        GitUtils.writeToFile(treeFileName, entry);

    }

    // accesses "tree" file from root folder
    // makes a blob object of the tree file
    // returns the blob object
    public Blob makeTreeBlob() throws IOException {
        if (!GitUtils.doesFileExist(treeFileName)) {
            throw new IOException("Error at makeTreeBlob(): This tree file \"" + treeFileName
                    + "\" does not exist!");
        }
        Blob blob = new Blob(treeFileName, TREE, true);

        return blob;
    }

    public void deleteTreeFile() {
        GitUtils.deleteFile(treeFileName);
    }

}
