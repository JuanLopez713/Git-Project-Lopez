import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Tree {

    private String treeName;
    private String treeSHA;

    public Tree() throws IOException {

        treeName = "tree";
        reset();
    }

    public Tree(String treeName) throws IOException {
        this.treeName = treeName;
        GitUtils.createFile("", treeName);
        reset();
    }

    public void addFile(String fileName) throws IOException {
        if (!GitUtils.doesFileExist(fileName)) {
            throw new IOException("File does not exist");
        }

        System.out.println("Adding file: " + fileName);
        Blob blob = new Blob(fileName);
        fileName = getFileName(fileName);
        String entry = "blob : " + blob.getSHA1() + " : " + fileName;
        GitUtils.appendToFile(treeName, entry);
    }

    public String getFileName(String entry) {
        if (entry.contains("/")) {
            String[] entryArray = entry.split("/");
            return entryArray[entryArray.length - 1];
        }

        return entry;
    }

    public void addDirectory(String directoryName) throws IOException {
        File directory = new File(directoryName);
        int treeCount = 0;
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }

        System.out.println("Adding directory: " + directoryName);
        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                String childTreeName = "tree" + (treeCount + 1);
                Tree childTree = new Tree(childTreeName);
                childTree.addDirectory(directoryName + "/" + file.getName());
                
                String entry = makeTreeEntry(childTreeName);
                GitUtils.appendToFile(treeName, entry);
                GitUtils.deleteFile(childTreeName);    
            } else {
                addFile(directoryName + "/" + file.getName());
            }
        }
        Blob blob = new Blob(treeName);
        treeSHA = blob.getSHA1();

    }

    public String makeTreeEntry(String treeName) throws IOException{
        Blob blob = new Blob(treeName);
        treeName = getFileName(treeName);
        String entry = "tree : " + blob.getSHA1() + " : " + treeName;
        return entry;
    }

    public void remove(String removedFileName) throws IOException {
        ArrayList<String> treeList = GitUtils.readFileToArrayListOfStrings("tree");
        for (String string : treeList) {
            String fileName = GitUtils.getFileName(string);
            if (fileName.equals(removedFileName)) {
                treeList.remove(string);
                GitUtils.writeArrayListOfStringsToFile(treeList, "tree");
                return;
            }
        }
    }

    public void reset(){
        GitUtils.deleteFile(treeName);
        GitUtils.createFile("", treeName);
    }

    public String getSHA1() {
        return treeSHA;
    }

}
