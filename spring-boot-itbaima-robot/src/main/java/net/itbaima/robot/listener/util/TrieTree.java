package net.itbaima.robot.listener.util;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 内部使用，处理关键词匹配AC自动机算法
 */
public class TrieTree {
    private final TrieNode root;

    public TrieTree() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.end = true;
    }

    public void buildFailureNode() {
        Queue<TrieNode> queue = new LinkedList<>();
        for (TrieNode child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            TrieNode current = queue.poll();
            for (char c : current.children.keySet()) {
                TrieNode child = current.children.get(c);
                queue.add(child);
                TrieNode failNode = current.fail;
                while (failNode != null && !failNode.children.containsKey(c))
                    failNode = failNode.fail;
                child.fail = failNode != null ? failNode.children.get(c) : root;
            }
        }
    }

    public boolean checkText(String text) {
        TrieNode current = root;
        for (char c : text.toCharArray()) {
            if (this.isInvalidChar(c)) continue;
            while (current != null && !current.children.containsKey(c))
                current = current.fail;
            if (current == null) {
                current = root;
                continue;
            }
            current = current.children.get(c);
            if (current.end) return true;
        }
        return false;
    }

    public int checkTextWithCount(String text) {
        Set<TrieNode> nodes = new HashSet<>();
        int count = 0;
        TrieNode current = root;
        for (char c : text.toCharArray()) {
            if (this.isInvalidChar(c)) continue;
            while (current != null && !current.children.containsKey(c))
                current = current.fail;
            if (current == null) {
                current = root;
                continue;
            }
            current = current.children.get(c);
            TrieNode tmp = current;
            while (tmp != null) {
                if(tmp.end && !nodes.contains(tmp)) {
                    nodes.add(tmp);
                    count++;
                }
                tmp = tmp.fail;
            }
        }
        return count;
    }

    private boolean isInvalidChar(char c) {
        String regex = "[^a-zA-Z0-9一-龥]";
        return Pattern.matches(regex, String.valueOf(c));
    }

    private static class TrieNode {
        HashMap<Character, TrieNode> children;
        TrieNode fail;
        boolean end;

        public TrieNode() {
            children = new HashMap<>();
        }
    }
}
