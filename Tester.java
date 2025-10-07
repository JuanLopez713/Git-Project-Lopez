import java.io.File;

public class Tester {
  public static void main(String[] args) {

    // Tree tree = new Tree();
    // tree.add("testFile");

    // tree.save();
    // Git.init();
    // GitUtils.createFile("folder", "test.txt");
    // GitUtils.writeToFile("folder/test.txt", "Hello, World!");

    // Tree.makeTree("project");
    // reset();
    cleanup();
    // Git.add("project/docs/test.txt");
    // Git.add("project/test2.txt");
    // Git.add("project/docs/test3.txt");

    // Tree.initializeWorkingListFile();
    // Tree.buildTreesFromWorkingList();

    // Git.add("project/docs/test.txt");
    // Git.add("project/test2.txt");

    // // GitUtils.createFile("folder", "test2.txt");
    // // GitUtils.writeToFile("folder/test2.txt", "Hello, World! 2");
    // // Git.remove("folder/test2.txt");

    // Git.add("project/docs/test3.txt");
    // Tree.initializeWorkingListFile(); // create git/temp with "blob <SHA> <path>"
    // lines
    // Tree.buildTreesFromWorkingList();
    // Git.add("testFolder");
    // Git.commit("Juan", "First commit");

    // System.out.println(GitUtils.hashFile("testFolder/3.txt"));
    // Git.commit("Juan", "First commit");

    // String commitSHA = GitUtils.readFileToString("refs/HEAD");
    // Git.checkout(commitSHA);

    // Git.edit("testFile/sample.txt");

  }

  public static void reset() {
    reset("git");
  }

  public static void reset(String directory) {
    // delete all the git directory
    File directoryFile = new File(directory);
    File[] files = directoryFile.listFiles();
    if (files == null) {
      return;
    }
    for (File file : files) {
      if (file.isDirectory()) {
        reset(file.getPath());
      }
      file.delete();
    }
    directoryFile.delete();
  }

  // clean up the git repository by deleting all blobs in the objects directory
  // and resetting the index file
  public static void cleanup() {
    cleanup("git");
  }

  public static void cleanup(String directory) {
    // delete all the blobs in the objects directory
    reset(directory);
    try {
      Git.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
