import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tree {

    // create git/temp with "blob <SHA> <path>" lines from index file
    public static void initializeWorkingListFile() throws IOException {
        String indexContent = GitUtils.readFileToString(Git.INDEX_PATH);

        
        StringBuilder tempContent = new StringBuilder();
        if (indexContent != null && !indexContent.isEmpty()) {
            String[] lines = indexContent.split("\n");
            for (String line : lines) {
                if (line == null || line.isEmpty()) {
                    continue;
                }
                tempContent.append("blob ").append(line).append('\n');
            }
        }

        GitUtils.createFile(Git.GIT_DIRECTORY, Git.TEMPFILE);
        GitUtils.writeToFile(Git.GIT_DIRECTORY + "/" + Git.TEMPFILE, tempContent.toString());
    }


    public static void createTreeFile(String directoryPath, String fileNameSHA1) {
        GitUtils.createFile(directoryPath, fileNameSHA1);
    }

    public static void add(String filePath, String entry) throws IOException {
        if (!GitUtils.doesFileExist(filePath)) {
            throw new IOException("The file " + filePath + " you are trying to add does not exist!");
        }
        // the entry should be appended to the tree file
        GitUtils.appendToFile(filePath, entry);
    }

    public static void parseIndexFile(String indexFilePath) throws IOException {
        String indexContent = GitUtils.readFileToString(indexFilePath);
        String[] entries = indexContent.split("\n");
        for (String entry : entries) {
            String[] parts = entry.split(" ");
            String filePath = parts[1];
            String sha1 = parts[0];
        }
    }

    public static void buildTreesFromWorkingList() throws IOException {
        String tempPath = Git.GIT_DIRECTORY + "/" + Git.TEMPFILE;
        String content = GitUtils.readFileToString(tempPath);
        if (content == null)
            content = "";

        List<String> lines = new ArrayList<>();
        for (String line : content.split("\n")) {
            if (!GitUtils.isEmpty(line)) {
                lines.add(line);
            }
        }

        while (true) {
            // Always sort by depth (deepest first) each iteration
            lines.sort((a, b) -> Integer.compare(pathDepth(pathOf(a)), pathDepth(pathOf(b))) * -1);
            // Stop if already collapsed to a single root tree entry
            if (lines.size() == 1 && isRootTreeLine(lines.get(0))) {
                break;
            }

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
}
