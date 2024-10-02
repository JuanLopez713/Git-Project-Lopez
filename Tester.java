public class Tester {
  public static void main(String[] args) throws Exception {

    // Tree tree = new Tree();
    // tree.add("testFile");

    // tree.save();
    Git.init();
    Git.add("testFolder");
    Git.commit("Juan", "First commit");

    // System.out.println(GitUtils.hashFile("testFolder/3.txt"));
    // Git.commit("Juan", "First commit");

    // String commitSHA = GitUtils.readFileToString("refs/HEAD");
    // Git.checkout(commitSHA);

    // Git.edit("testFile/sample.txt");


  }
}
