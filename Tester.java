public class Tester {
  public static void main(String[] args) throws Exception {

    // Tree tree = new Tree();
    // tree.add("testFile");

    // tree.save();
    // Git.init();
    // GitUtils.createFile("folder", "test.txt");
    // GitUtils.writeToFile("folder/test.txt", "Hello, World!");

     Git.add("folder/test.txt");
     Git.add("folder/test2.txt");

    // GitUtils.createFile("folder", "test2.txt");
    // GitUtils.writeToFile("folder/test2.txt", "Hello, World! 2");
    // Git.remove("folder/test2.txt");

    Git.add("folder/docs/test3.txt");
    Tree.initializeWorkingListFile(); // create git/temp with "blob <SHA> <path>" lines
    Tree.buildTreesFromWorkingList();
    // Git.add("testFolder");
    // Git.commit("Juan", "First commit");

    // System.out.println(GitUtils.hashFile("testFolder/3.txt"));
    // Git.commit("Juan", "First commit");

    // String commitSHA = GitUtils.readFileToString("refs/HEAD");
    // Git.checkout(commitSHA);

    // Git.edit("testFile/sample.txt");

  }
}
