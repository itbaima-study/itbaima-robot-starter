package net.itbaima.robot.service;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.data.UserProfile;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Message;

import java.util.function.Consumer;

public interface RobotService {
    MessageReceipt<Friend> sendMessageToFriend(long user, Message message);

    void run(Consumer<Bot> action);

    void runWithFriend(long group, Consumer<Friend> action);

    void runWithGroup(long group, Consumer<Group> action);

    void runWithGroupMembers(long group, Consumer<ContactList<NormalMember>> action);

    void runWithProfile(long user, Consumer<UserProfile> action);

    MessageReceipt<Friend> sendMessageToFriend(long user, String message);
    void deleteFriend(long user);
    Friend getFriend(long user);
    MessageReceipt<Group> sendMessageToGroup(long group, Message message);
    MessageReceipt<Group> sendMessageToGroup(long group, String message);
    void deleteGroup(long group);
    Group getGroup(long group);
    boolean isGroupContainsUser(long group, long user);
}
