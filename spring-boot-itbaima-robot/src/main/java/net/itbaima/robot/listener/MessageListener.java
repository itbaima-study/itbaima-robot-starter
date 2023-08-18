package net.itbaima.robot.listener;

import net.itbaima.robot.listener.util.TrieTree;
import net.mamoe.mirai.message.data.*;

import java.util.Collections;
import java.util.List;

/**
 * 消息监听器通用抽象，包含一些封装好的消息管理相关方法，比如违禁词检测（基于AC自动机实现）等
 *
 * <p>如果您编写的是消息监听相关的监听器，那么可以直接继承此类并获得所有
 * 已经封装好的工具方法，可以直接使用。
 *
 *
 * @since 1.0.0
 * @author Ketuer
 */
public abstract class MessageListener {

    private TrieTree tree;
    private final boolean caseSensitive;

    /**
     * 构造普通的抽象消息监听器
     * @since 1.0.0
     */
    public MessageListener() {
        this(Collections.emptyList(), false);
    }

    /**
     * 在构造时直接填写后续用于判断的违禁词列表
     * @since 1.0.0
     * @param caseSensitive 是否大小写敏感
     * @param words 违禁词列表
     */
    public MessageListener(List<String> words, boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        if(!words.isEmpty())
            this.setProhibitedWords(words);
    }

    /**
     * 设置违禁词列表，后续可以使用 {@link  #invalidTextWithCount} 方法快速进行违禁词判断
     * @since 1.0.0
     * @param words 违禁词
     */
    protected void setProhibitedWords(List<String> words){
        tree = new TrieTree();
        words.forEach(word -> tree.insert(caseSensitive ? word.toLowerCase() : word));
        tree.buildFailureNode();
    }

    /**
     * 判断一段文本中违禁词出现的次数（单个违禁词只会统计一次）
     * @since 1.0.0
     * @param text 文本内容
     * @return 出现违禁词次数
     */
    protected int invalidTextWithCount(String text){
        return tree.checkTextWithCount(caseSensitive ? text.toLowerCase() : text);
    }

    /**
     * 判断消息中是否出现违禁词，仅判断文本内容（单个违禁词只会统计一次）
     * @since 1.0.0
     * @param message Message
     * @return 出现违禁词次数
     */
    protected int invalidTextWithCount(Message message){
        return this.invalidTextWithCount(message.contentToString());
    }

    /**
     * 判断一段文本中是否出现违禁词
     * @since 1.0.0
     * @param text 文本内容
     * @return 是否出现
     */
    protected boolean invalidText(String text){
        return tree.checkText(caseSensitive ? text.toLowerCase() : text);
    }

    /**
     * 判断消息中是否出现违禁词，仅判断文本内容
     * @since 1.0.0
     * @param message 消息
     * @return 是否出现
     */
    protected boolean invalidText(Message message){
        return this.invalidText(message.contentToString());
    }

    /**
     * 撤回一条消息
     * @since 1.0.0
     * @param messages 消息
     */
    protected void recallMessage(MessageChain messages){
        MessageSource.recall(messages);
    }

    /**
     * 对一条消息进行引用，并根据给定的文本构造成一个引用回复消息
     * @since 1.0.0
     * @param quote 引用
     * @param message 回复内容
     * @return 构造完成的消息链
     */
    protected MessageChain quoteWithMessages(MessageChain quote, String... message){
        QuoteReply quoteReply = MessageSource.quote(quote);
        MessageChain chain = MessageUtils.newChain(quoteReply);
        for (String s : message)
            chain.plus(new PlainText(s));
        return chain;
    }

    /**
     * 对一条消息进行引用，并根据给定的消息链进行合并得到最终的引用回复消息
     * @since 1.0.0
     * @param quote 引用
     * @param messages 回复消息
     * @return 构造完成的消息链
     */
    protected MessageChain quoteWithMessages(MessageChain quote, Message... messages){
        QuoteReply quoteReply = MessageSource.quote(quote);
        MessageChain chain = MessageUtils.newChain(quoteReply);
        for (Message message : messages)
            chain.plus(message);
        return chain;
    }

    /**
     * 对一个用户进行@操作，并根据给定的文本构造成一个引用回复消息
     * @since 1.0.0
     * @param user 用户
     * @param message 回复内容
     * @return 构造完成的消息链
     */
    protected MessageChain atWithMessages(long user, String... message){
        At at = new At(user);
        MessageChain chain = MessageUtils.newChain(at);
        for (String s : message)
            chain.plus(new PlainText(s));
        return chain;
    }

    /**
     * 对一个用户进行@操作，并根据给定的消息链进行合并得到最终的引用回复消息
     * @since 1.0.0
     * @param user 用户
     * @param messages 回复消息
     * @return 构造完成的消息链
     */
    protected MessageChain atWithMessages(long user, Message... messages){
        At at = new At(user);
        MessageChain chain = MessageUtils.newChain(at);
        for (Message message : messages)
            chain.plus(message);
        return chain;
    }
}
