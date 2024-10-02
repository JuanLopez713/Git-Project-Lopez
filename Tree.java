import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Tree {

    private static final Blob.Type TREE = Blob.Type.TREE;

    private String objectFoldePath = "git/objects";
    private String folderPath;
    private String treeFileContents;

    private ArrayList<Blob> treeEntries;


    // constructors
    public Tree() throws IOException {

        treeEntries = new ArrayList<>();
        folderPath = "tree";

    }

   

    // constructor for restoring TREE files
    public Tree(String folderPath) throws IOException {

        this.treeEntries = new ArrayList<>();
        this.folderPath = folderPath;
        System.out.println("Creating tree: " + folderPath);

    }

    public void restore() throws IOException {
        System.out.println("Need to implement restore...");
        // System.out.println("Rebuilding Folder: " + folderPath);
        // this.treeFileContents = GitUtils.readFileToString(treeFilePath);
        // String[] treeFileLines = treeFileContents.split("\n");

        // GitUtils.createDirectory(folderPath);

        // for (String line : treeFileLines) {

        //     String[] lineParts = line.split(":");

        //     if (line.startsWith("tree")) {
        //         String subFolder = folderPath + "/" + lineParts[2].trim();
        //         Tree childTree = new Tree(lineParts[1].trim(), subFolder);
        //         childTree.restore();

        //     } else {
        //         Blob blob = new Blob(lineParts);
        //         blob.setFolderPath(folderPath);
        //         blob.restoreFile();
        //         treeEntries.add(blob);

        //     }

        // }
    }

    // method -> add
    public void add(String filePath, String fileSHA1) throws IOException {

        if (!GitUtils.doesFileExist(filePath)) {
            throw new IOException("File does not exist");
        }

        if (GitUtils.isDirectory(filePath)) {
            addDirectory(filePath, fileSHA1);
            return;
        }

        addFile(filePath, fileSHA1);

    }

    // method -> addFile
    public void addFile(String filePath, String fileSha1) throws IOException {

        if (!GitUtils.doesFileExist(filePath)) {
            throw new IOException("File does not exist");
        }

        Blob blob = new Blob(filePath, fileSha1, Blob.Type.BLOB);
        blob.save();
        treeEntries.add(blob);
    }

    // method -> addDirectory
    public void addDirectory(String folderName, String fileSha1) throws IOException {

        if (!GitUtils.isDirectory(folderName)) {
            throw new IllegalArgumentException("Not a directory");
        }

        // System.out.println("Adding directory: " + folderName);

        String[] files = GitUtils.getFiles(folderName);
        GitUtils.printStringArray(files);
        // for (String file : files) {
        //     if (GitUtils.isDirectory(folderName + "/" + file)) {

        //         // build the sub tree and save file
        //         String subTreeFileName = file.getName();
        //         String subDirectory = folderName + "/" + subTreeFileName;
        //         subTreeFileName = file.getName();

        //         Tree subTree = new Tree(subTreeFileName);
        //         subTree.addDirectory(subDirectory);
        //         subTree.saveTreeFile();

        //         // make a blob of the sub tree
        //         // add blob to the treeEntries
        //         // delete the sub tree file
        //         Blob blob = subTree.makeTreeBlob();
        //         treeEntries.add(blob);
        //         subTree.deleteTreeFile();

        //     } else {
        //         addFile(folderName + "/" + file.getName());
        //     }
        // }

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
        // for (Blob blob : treeEntries) {
        //     if (blob.getFileName().equals(fileName)) {
        //         treeEntries.remove(blob);
        //         return;
        //     }
        // }
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
        System.out.println("Saving tree: " + folderPath);
        GitUtils.createFile("", objectFoldePath);
        String entry = "";
        for (Blob blob : treeEntries) {
            entry += blob.toString() + "\n";
        }
        entry = entry.substring(0, entry.length() - 1);
        GitUtils.writeToFile(objectFoldePath, entry);

    }

    // accesses "tree" file from root folder
    // makes a blob object of the tree file
    // returns the blob object
    public Blob makeTreeBlob() throws IOException {
        // if (!GitUtils.doesFileExist(treeFileName)) {
        //     throw new IOException("Error at makeTreeBlob(): This tree file \"" + treeFileName
        //             + "\" does not exist!");
        // }
        // Blob blob = new Blob(treeFileName, TREE, true);

        // return blob;
        System.out.println("Need to implement makeTreeBlob...");
        return null;
    }

    public void deleteTreeFile() {
        GitUtils.deleteFile(folderPath);
    }

}
