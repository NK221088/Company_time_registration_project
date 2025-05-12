package dtu.timemanager.gui.helper;

import javafx.scene.control.TreeItem;
import java.util.HashSet;
import java.util.Set;

public class TreeViewHelper {
    public static Set<String> getExpandedPaths(TreeItem<Object> root) {
        Set<String> expandedPaths = new HashSet<>();
        collectExpandedPaths(root, "", expandedPaths);
        return expandedPaths;
    }

    private static void collectExpandedPaths(TreeItem<Object> node, String path, Set<String> paths) {
        if (node.isExpanded()) {
            paths.add(path);
        }
        for (TreeItem<Object> child : node.getChildren()) {
            Object value = child.getValue();
            String childName = (value != null) ? value.toString() : "null";
            collectExpandedPaths(child, path + "/" + childName, paths);
        }
    }

    public static void restoreExpandedPaths(TreeItem<Object> root, Set<String> expandedPaths) {
        expandPaths(root, "", expandedPaths);
    }

    private static void expandPaths(TreeItem<Object> node, String path, Set<String> expandedPaths) {
        if (expandedPaths.contains(path)) {
            node.setExpanded(true);
        }
        for (TreeItem<Object> child : node.getChildren()) {
            Object value = child.getValue();
            String childName = (value != null) ? value.toString() : "null";
            expandPaths(child, path + "/" + childName, expandedPaths);
        }
    }
}