public class Tester {
  public static void main(String[] args) throws Exception {

    // Tree tree = new Tree();
    // tree.add("testFile");

    //  tree.save();
   // Git.init();
  // Git.add("testFile");
   // Git.commit("Juan", "First commit");

    String commitSHA = GitUtils.readFileToString("refs/HEAD");
    Git.checkout(commitSHA);

   // Git.edit("testFile/sample.txt");


  }
}
