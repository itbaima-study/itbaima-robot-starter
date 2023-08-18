package net.itbaima.robot.service.impl;

import jakarta.annotation.Resource;
import net.itbaima.robot.service.RobotService;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.IMirai;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.data.UserProfile;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Message;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 可以快速使用的RobotService服务类，包含大量通用操作
 *
 * @since 1.0.0
 * @author Ketuer
 */
@Service
public class RobotServiceImpl implements RobotService {

    @Resource
    private Bot bot;

    @Resource
    private IMirai mirai;

    /**
     * 在回调中进行聊天机器人相关操作
     * @param action 操作
     */
    @Override
    public void run(Consumer<Bot> action) {
        action.accept(bot);
    }

    /**
     * 在回调中对指定好友进行操作
     * @param fried 好友QQ号
     * @param action 操作
     */
    @Override
    public void runWithFriend(long fried, Consumer<Friend> action) {
        action.accept(this.getFriend(fried));
    }

    /**
     * 在回调中对指定群聊进行操作
     * @param group 群号
     * @param action 操作
     */
    @Override
    public void runWithGroup(long group, Consumer<Group> action) {
        action.accept(this.getGroup(group));
    }

    /**
     * 在回调中对指定群成员列表进行操作
     * @param group 群号
     * @param action 操作
     */
    @Override
    public void runWithGroupMembers(long group, Consumer<ContactList<NormalMember>> action) {
        action.accept(this.getGroup(group).getMembers());
    }

    /**
     * 查询用户信息，并在回调中对指定用户信息进行操作
     * @param user 用户QQ号
     * @param action 操作
     */
    @Override
    public void runWithProfile(long user, Consumer<UserProfile> action){
        action.accept(mirai.queryProfile(bot, user));
    }

    /**
     * 向指定好友发送一条文本消息
     * @param friend 好友
     * @param message 文本消息
     * @return 消息回执
     */
    @Override
    public MessageReceipt<Friend> sendMessageToFriend(long friend, String message){
        return this.findFriendById(friend).sendMessage(message);
    }

    /**
     * 向指定好友发送一条消息
     * @param friend 好友
     * @param message 消息
     * @return 消息回执
     */
    @Override
    public MessageReceipt<Friend> sendMessageToFriend(long friend, Message message){
        return this.findFriendById(friend).sendMessage(message);
    }

    /**
     * 删除指定ID的好友
     * @param friend 好友
     */
    @Override
    public void deleteFriend(long friend){
        this.findFriendById(friend).delete();
    }

    /**
     * 获取指定ID的好友，不存在时返回null
     * @param friend 好友
     */
    @Override
    public Friend getFriend(long friend){
        return bot.getFriend(friend);
    }

    /**
     * 发送一条消息到群聊
     * @param group 群号
     * @param message 消息
     * @return 消息回执
     */
    @Override
    public MessageReceipt<Group> sendMessageToGroup(long group, Message message) {
        return this.findGroupById(group).sendMessage(message);
    }

    /**
     * 发送一条文本消息到群聊
     * @param group 群号
     * @param message 消息
     * @return 消息回执
     */
    @Override
    public MessageReceipt<Group> sendMessageToGroup(long group, String message) {
        return this.findGroupById(group).sendMessage(message);
    }

    /**
     * 退出指定ID的群聊
     * @param group 群号
     */
    @Override
    public void deleteGroup(long group) {
        this.findGroupById(group).quit();
    }

    /**
     * 获取指定ID的群里，不存在时返回null
     * @param group 群号
     * @return 群
     */
    @Override
    public Group getGroup(long group) {
        return bot.getGroup(group);
    }

    /**
     * 快速判断某个群内是否存在指定QQ号的用户
     * @param group 群号
     * @param user QQ号
     * @return 是否存在
     */
    @Override
    public boolean isGroupContainsUser(long group, long user) {
        return this.findGroupById(group).contains(user);
    }

    private Friend findFriendById(long id){
        return bot.getFriendOrFail(id);
    }

    private Group findGroupById(long id){
        return bot.getGroupOrFail(id);
    }
}
