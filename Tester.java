public class Tester {
    public static void main(String[] args) throws Exception{
        GitUtils.createFile("", "test.txt");
        GitUtils.writeToFile("test.txt", "hello world");

        Blob b = new Blob("test.txt");
    }
}
