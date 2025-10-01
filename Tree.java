import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tree {

    private static final Blob.Type TREE = Blob.Type.TREE;

    private Map<String, String> treeDirectories;
    private Map<String, String> treeFiles;
    private String objectFolderPath = "git/objects";
    private String folderPath;
    private String treeFileContents;

    // constructors
    public Tree() throws IOException {

        treeDirectories = new HashMap<>();
        treeFiles = new HashMap<>();
        folderPath = "tree";

    }

    // constructor for restoring TREE files
    public Tree(String folderPath) throws IOException {

        System.out.println("Creating tree: " + folderPath);

        this.treeDirectories = new HashMap<>();
        this.treeFiles = new HashMap<>();
        this.folderPath = folderPath;

    }

    public void restore() throws IOException {
        System.out.println("Need to implement restore...");
        // System.out.println("Rebuilding Folder: " + folderPath);
        // this.treeFileContents = GitUtils.readFileToString(treeFilePath);
        // String[] treeFileLines = treeFileContents.split("\n");

        // GitUtils.createDirectory(folderPath);

        // for (String line : treeFileLines) {

        // String[] lineParts = line.split(":");

        // if (line.startsWith("tree")) {
        // String subFolder = folderPath + "/" + lineParts[2].trim();
        // Tree childTree = new Tree(lineParts[1].trim(), subFolder);
        // childTree.restore();

            } else {
                Blob blob = new Blob(lineParts);
                blob.setObjectDirectoryPath(folderPath);
                blob.restoreFile();
                treeEntries.add(blob);

        // }

        // }
    }

    // method -> add
    public void add(String sha1, String filePath) throws IOException {

        if (!GitUtils.doesFileExist(filePath)) {
            throw new IOException("File does not exist");
        }

        // if (GitUtils.isDirectory(filePath)) {
        // addDirectory(filePath, blob);
        // return;
        // }

        addFile(sha1, filePath);

    }

    // method -> addFile
    public void addFile(String sha1, String filePath) throws IOException {

        if (!GitUtils.doesFileExist(filePath)) {
            throw new IOException("File does not exist");
        }

        treeFiles.put(sha1, filePath);
    }

    // method -> addDirectory
    public void addDirectory(String folderName, Tree directory) throws IOException {

        // if (!GitUtils.isDirectory(folderName)) {
        // throw new IllegalArgumentException("Not a directory");
        // }

        // // System.out.println("Adding directory: " + folderName);

        // String[] files = GitUtils.getFiles(folderName);
        // GitUtils.printStringArray(files);
        // for (String file : files) {
        // if (GitUtils.isDirectory(folderName + "/" + file)) {

        // // build the sub tree and save file
        // String subTreeFileName = file.getName();
        // String subDirectory = folderName + "/" + subTreeFileName;
        // subTreeFileName = file.getName();

        // Tree subTree = new Tree(subTreeFileName);
        // subTree.addDirectory(subDirectory);
        // subTree.saveTreeFile();

        // // make a blob of the sub tree
        // // add blob to the treeEntries
        // // delete the sub tree file
        // Blob blob = subTree.makeTreeBlob();
        // treeEntries.add(blob);
        // subTree.deleteTreeFile();

        // } else {
        // addFile(folderName + "/" + file.getName());
        // }
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
        // if (blob.getFileName().equals(fileName)) {
        // treeEntries.remove(blob);
        // return;
        // }
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
        // for (Blob blob : treeFiles) {

        // blob.restoreFile();
        // }
    }

    public void saveTreeFile() throws IOException {
        // System.out.println("Saving tree: " + folderPath);
        // GitUtils.createFile("", objectFolderPath);
        // String entry = "";
        // for (Blob blob : treeFileContents) {
        // entry += blob.toString() + "\n";
        // }
        // entry = entry.substring(0, entry.length() - 1);
        // GitUtils.writeToFile(objectFolderPath, entry);

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

        // return blob;
        System.out.println("Need to implement makeTreeBlob...");
        return null;
    }

    public void deleteTreeFile() {
        GitUtils.deleteFile(folderPath);
    }



}
