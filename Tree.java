import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileNotFoundException;

public class Tree {

    // makes the tree files from the directory
    public static void makeTree(String directoryPath) throws IOException {
        // makes the file and initializes important items
        File treeFile = new File(directoryPath);
        if (!treeFile.exists() || !treeFile.isDirectory()) {
            throw new FileNotFoundException("Directory not found: " + directoryPath);
        }

        File[] files = treeFile.listFiles();
        if (files == null) {
            throw new IOException("Unable to list files for: " + directoryPath);
        }
        StringBuilder treeBuilder = new StringBuilder();

        for (File innerFile : files) {
            if (innerFile.isDirectory()) {
                makeTree(innerFile.getPath());
            } else {
                treeBuilder.append("blob").append(innerFile.hashCode()).append(innerFile.getName()).append("\n");
            }
        }

        String toWrite = treeBuilder.toString();

        File officialTree = new File(Git.OBJECTS_DIRECTORY + "/" + GitUtils.hashFile(toWrite));
        officialTree.createNewFile();

        try {
            Files.write(Paths.get(officialTree.getAbsolutePath()), toWrite.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // create git/temp with "blob <SHA> <path>" lines from index file
    public static void initializeWorkingListFile() throws IOException {
        String indexContent = GitUtils.readFileToString(Git.INDEX_PATH);

        // Initialize the working list content by appending "blob <SHA> <path>" lines to
        // the StringBuilder
        StringBuilder workingListContent = new StringBuilder();
        if (indexContent != null && !indexContent.isEmpty()) {

            // If the entry is empty, continue
            // If the entry is not empty, append "blob <SHA> <path>" lines to the
            // StringBuilder
            String[] entries = indexContent.split("\n");
            for (String entry : entries) {
                if (GitUtils.isEmpty(entry)) {
                    continue;
                }
                workingListContent.append("blob ").append(entry).append('\n');
            }
        }

        // Create the working list file and write the content to it
        GitUtils.createFile(Git.GIT_DIRECTORY, Git.WORKING_LIST_FIlE);
        GitUtils.writeToFile(Git.GIT_DIRECTORY + "/" + Git.WORKING_LIST_FIlE, workingListContent.toString());
    }

    public static void buildTreesFromWorkingList() throws IOException {
        // read the temp file and split it into lines
        String tempPath = Git.GIT_DIRECTORY + "/" + Git.WORKING_LIST_FIlE;
        String content = GitUtils.readFileToString(tempPath);
        if (content == null)
            content = "";

        List<String> lines = new ArrayList<>();
        for (String line : content.split("\n")) {
            if (!GitUtils.isEmpty(line)) {
                lines.add(line);
            }
        }

        // build the trees from the working list
        while (true) {
            // Always sort by depth (deepest first) each iteration
            lines.sort((a, b) -> Integer.compare(pathDepth(pathOf(a)), pathDepth(pathOf(b))) * -1);
            // Stop if already collapsed to a single root tree entry
            if (lines.size() == 1 && isRootTreeLine(lines.get(0))) {
                break;
            }

            // find the max depth of the trees
            int maxDepth = -1;
            for (String line : lines) {
                String p = pathOf(line);
                if (isRootPath(p))
                    continue;
                int d = pathDepth(p);
                if (d > maxDepth)
                    maxDepth = d;
            }

            if (maxDepth < 0) {
                // Nothing to group/collapse; prevent infinite loop
                break;
            }

            // Group entries at maxDepth by their parent directory (preserve order)
            Map<String, List<String>> parentToEntries = new LinkedHashMap<>();
            for (String line : lines) {
                String p = pathOf(line);
                if (isRootPath(p))
                    continue;
                if (pathDepth(p) == maxDepth) {
                    String parent = parentDir(p);
                    parentToEntries.computeIfAbsent(parent, k -> new ArrayList<>()).add(line);
                }
            }

            if (parentToEntries.isEmpty()) {
                break;
            }

            // Build new working list: keep non-maxDepth lines, then add collapsed tree
            // lines
            List<String> nextLines = new ArrayList<>();
            for (String line : lines) {
                String p = pathOf(line);
                if (!isRootPath(p) && pathDepth(p) == maxDepth) {
                    // Skip entries that will be collapsed
                    continue;
                }
                nextLines.add(line);
            }

            for (Map.Entry<String, List<String>> group : parentToEntries.entrySet()) {
                String parent = group.getKey();

                // Build tree content from ALL current direct children of this parent at this
                // depth, using only the entry name (not full path)
                StringBuilder treeContent = new StringBuilder();
                for (String candidate : lines) {
                    String cp = pathOf(candidate);
                    if (!isRootPath(cp) && pathDepth(cp) == maxDepth && parentDir(cp).equals(parent)) {
                        String type = entryType(candidate);
                        String sha = entrySha(candidate);
                        String name = baseName(cp);
                        treeContent.append(type).append(' ').append(sha).append(' ').append(name).append('\n');
                    }
                }

                String treeSha = sha1Hex(treeContent.toString());

                // Persist tree object in objects directory
                GitUtils.createFile(Git.OBJECTS_DIRECTORY, treeSha);
                GitUtils.writeToFile(Git.OBJECTS_DIRECTORY + "/" + treeSha, treeContent.toString());

                String parentLabel = isRootPath(parent) ? "(root)" : parent;
                nextLines.add("tree " + treeSha + " " + parentLabel);
            }

            lines = nextLines;

            // Persist working list after each collapse step
            StringBuilder updated = new StringBuilder();
            for (String l : lines) {
                updated.append(l).append('\n');
            }
            GitUtils.writeToFile(tempPath, updated.toString());

        }
    }

    private static String pathOf(String line) {
        int firstSpace = line.indexOf(' ');
        if (firstSpace < 0)
            return "";
        int secondSpace = line.indexOf(' ', firstSpace + 1);
        if (secondSpace < 0)
            return "";
        return line.substring(secondSpace + 1);
    }

    private static String entryType(String line) {
        int firstSpace = line.indexOf(' ');
        return firstSpace < 0 ? "" : line.substring(0, firstSpace);
    }

    private static String entrySha(String line) {
        int firstSpace = line.indexOf(' ');
        if (firstSpace < 0)
            return "";
        int secondSpace = line.indexOf(' ', firstSpace + 1);
        if (secondSpace < 0)
            return "";
        return line.substring(firstSpace + 1, secondSpace);
    }

    private static String baseName(String path) {
        if (GitUtils.isEmpty(path) || isRootPath(path))
            return path;
        int idx = path.lastIndexOf('/');
        return idx < 0 ? path : path.substring(idx + 1);
    }

    private static int pathDepth(String path) {
        if (isRootPath(path) || GitUtils.isEmpty(path))
            return 0;
        int depth = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/')
                depth++;
        }
        return depth;
    }

    private static String parentDir(String path) {
        if (isRootPath(path) || GitUtils.isEmpty(path))
            return "(root)";
        int idx = path.lastIndexOf('/');
        if (idx < 0)
            return "(root)";
        return path.substring(0, idx);
    }

    private static boolean isRootPath(String path) {
        return "(root)".equals(path);
    }

    private static boolean isRootTreeLine(String line) {
        return line.startsWith("tree ") && pathOf(line).equals("(root)");
    }

    private static String sha1Hex(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            md.update(bytes);
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1)
                    sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRootTreeSha() {

        String rootTreeEntry = GitUtils.readFileToString(Git.GIT_DIRECTORY + "/" + Git.WORKING_LIST_FIlE);
        String rootTreeSha = rootTreeEntry.split(" ")[1];
        return rootTreeSha;
    }
}
