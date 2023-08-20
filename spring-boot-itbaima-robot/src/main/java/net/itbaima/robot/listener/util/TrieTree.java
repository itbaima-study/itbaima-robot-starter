package net.itbaima.robot.listener.util;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 内部使用，处理关键词匹配AC自动机算法
 */
public class TrieTree {
    private final TrieNode root;

    // 静态成员，预编译的正则表达式
    private static final Pattern INVALID_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9\u4E00-\u9FA5]");

    // 创建一个新的Trie树，根节点为空
    public TrieTree() {
        root = new TrieNode();
    }

    // 插入一个新的关键词到Trie树中
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode()); // 如果该字符在当前节点的子节点中不存在，则创建一个新的子节点
            node = node.children.get(c); // 移动到下一个子节点
        }
        node.end = true; // 标记最后一个字符的节点为结束节点，表示一个完整的关键词
    }

    // 构建AC自动机的失效链接
    public void buildFailureNode() {
        Queue<TrieNode> queue = new LinkedList<>();
        for (TrieNode child : root.children.values()) {
            child.fail = root; // 根节点的子节点的失效链接都指向根节点
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            TrieNode current = queue.poll();
            for (char c : current.children.keySet()) {
                TrieNode child = current.children.get(c);
                queue.add(child);
                TrieNode failNode = current.fail;
                while (failNode != null && !failNode.children.containsKey(c))
                    failNode = failNode.fail; // 寻找失效链接的节点
                child.fail = failNode != null ? failNode.children.get(c) : root; // 如果找到了失效链接的节点，则指向该节点的对应子节点，否则指向根节点
            }
        }
    }

    // 检查文本中是否存在关键词
    public boolean checkText(String text) {
        TrieNode current = root;
        for (char c : text.toCharArray()) {
            if (isInvalidChar(c)) continue; // 如果字符无效，则跳过
            while (current != null && !current.children.containsKey(c))
                current = current.fail; // 如果当前节点的子节点中不存在该字符，则跟随失效链接向上查找
            if (current == null) {
                current = root; // 如果没有找到，则回到根节点并继续查找
                continue;
            }
            current = current.children.get(c); // 如果找到了，则转到下一个子节点
            if (current.end) return true; // 如果找到了一个关键词的结束节点，则返回true
        }
        return false; // 如果没有找到任何关键词，则返回false
    }

    // 检查文本中的关键词数量
    public int checkTextWithCount(String text) {
        Set<TrieNode> nodes = new HashSet<>();
        int count = 0;
        TrieNode current = root;
        for (char c : text.toCharArray()) {
            if (isInvalidChar(c)) continue; // 无效字符直接跳过
            while (current != null && !current.children.containsKey(c))
                current = current.fail; // 如果当前节点的子节点中不存在该字符，则跟随失效链接向上查找
            if (current == null) {
                current = root; // 如果没有找到，则回到根节点并继续查找
                continue;
            }
            current = current.children.get(c); // 如果找到了，则转到下一个子节点
            TrieNode tmp = current;
            while (tmp != null) {
                if(tmp.end && !nodes.contains(tmp)) {
                    nodes.add(tmp); // 如果找到了一个关键词的结束节点，并且该节点还没有被计数过，则计数加1
                    count++;
                }
                tmp = tmp.fail; // 向上跟随失效链接查找其他可能的关键词
            }
        }
        return count; // 返回找到的关键词数量
    }

    // 检查字符是否为无效字符，这里定义了无效字符为非英文、非数字和非中文的字符
    private boolean isInvalidChar(char c) {
        return INVALID_CHAR_PATTERN.matcher(String.valueOf(c)).matches();
    }

    // Trie树的节点类，包含子节点、失效链接和结束标记
    private static class TrieNode {
        HashMap<Character, TrieNode> children;
        TrieNode fail;
        boolean end;

        public TrieNode() {
            children = new HashMap<>();
        }
    }
}
