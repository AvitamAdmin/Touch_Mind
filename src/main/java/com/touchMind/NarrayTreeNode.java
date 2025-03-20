package com.touchMind;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class NarrayTreeNode {
    String category;
    String val;  // Value of the node
    List<NarrayTreeNode> children;  // List of children nodes

    // Constructor
    public NarrayTreeNode(String category, String val) {
        this.val = val;
        children = new ArrayList<>();  // Initialize the children list
    }

    // Add a child to the current node
    public void addChild(NarrayTreeNode child) {
        children.add(child);
    }

    // DFS traversal of N-ary Tree (Preorder Traversal)
    public void dfs(NarrayTreeNode root) {
        if (root == null) return;

        // Process the current node (e.g., print its value)
        System.out.print(root.category + " : " + root.val + " > ");

        // Recur for all children of this node
        for (NarrayTreeNode child : root.children) {
            dfs(child);
        }
    }

    public NarrayTreeNode constructTree() {
        Map<String, List<String>> navigationTree = new LinkedHashMap<>();
        String filePath = "notebook.txt"; // Replace with the file path
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(line -> {
                String[] properties = line.split(":");
                //System.out.println(properties[properties.length-2] + " : " +properties[properties.length-1]);
                if (navigationTree.containsKey(properties[properties.length - 2])) {
                    List<String> children = navigationTree.get(properties[properties.length - 2]);
                    children.add(properties[properties.length - 2] + ":" + properties[properties.length - 1]);
                } else {
                    List<String> children = new ArrayList<>();
                    children.add(properties[properties.length - 2] + ":" + properties[properties.length - 1]);
                    navigationTree.put(properties[properties.length - 2], children);
                }
            });

            Iterator<String> iterator = navigationTree.keySet().iterator();
            String nextKey = null;

            NarrayTreeNode root = null;
            if (iterator.hasNext()) {
                nextKey = iterator.next();  // Initialize first key
                root = new NarrayTreeNode(nextKey, nextKey);
            }

            while (nextKey != null) {
                String currentKey = nextKey;
                NarrayTreeNode currentChild = root;
                List<NarrayTreeNode> currentChildren = root.children;
                nextKey = iterator.hasNext() ? iterator.next() : null;
                List<String> nextChildren = navigationTree.get(nextKey);
                currentChildren.stream().forEach(cChild -> {

                });
                System.out.println("Current Key: " + currentKey + ", Next Key: " + (nextKey != null ? nextKey : "No Next Key"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
