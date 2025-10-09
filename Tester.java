import java.io.File;

public class Tester {
  public static void main(String[] args) throws Exception {

    testCommitTwice();

  }

  // delete the git repository
  public static void reset() {
    GitUtils.reset("git");
  }

  // clean up the git repository by deleting all blobs in the objects directory
  // and resetting the index file
  public static void cleanup() {
    GitUtils.cleanup("git");
  }

  public static void testCommitOnce() {
    reset();
    // Initialize the Git repository
    Git.init();

    // Create files
    createFiles();

    // Add files to the index
    Git.add("project/file1.txt");
    Git.add("project/file2.txt");
    Git.add("project/docs/file3.txt");

    Git.commit("Juan", "First commit");
  }

  public static void testCommitTwice() {
    reset();
    // Initialize the Git repository
    Git.init();

    // Create files
    createFiles();

    // Add files to the index
    Git.add("project/file1.txt");
    Git.add("project/file2.txt");
    Git.add("project/docs/file3.txt");

    Git.commit("Juan", "First commit");

    GitUtils.writeToFile("project/file1.txt", "Hello, World! 5");

    Git.add("project/file1.txt");

    Git.commit("Juan", "Second commit");
  }

  public static void createFiles() {
    GitUtils.createFile("project", "file1.txt");
    GitUtils.writeToFile("project/file1.txt", "Hello, World!");
    GitUtils.createFile("project", "file2.txt");
    GitUtils.writeToFile("project/file2.txt", "Hello, World! 2");
    GitUtils.createFile("project/docs", "file3.txt");
    GitUtils.writeToFile("project/docs/file3.txt", "Hello, World! 3");
    GitUtils.createFile("project/docs", "file4.txt");
    GitUtils.writeToFile("project/docs/file4.txt", "Hello, World! 4");
  }

}
