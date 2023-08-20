 ![mirai](/Users/nagocoler/Downloads/mirai.svg)

本项目基于 Mirai ，它是一个在全平台下运行，提供 QQ Android 协议支持的高效率机器人库。

- mirai 是完全免费且开放源代码的软件，仅供学习和娱乐用途使用
- mirai 不会通过任何方式强制收取费用，或对使用者提出物质条件
- mirai 由整个开源社区维护，并不是属于某个个体的作品，所有贡献者都享有其作品的著作权。

原 Mirai 项目地址：https://github.com/mamoe/mirai

## itbaima-robot-starter

**声明：本项目仅用于学习交流使用，请勿用于任何非法用途**

本项目因为基于`mirai` ，同样采用 `AGPLv3` 协议开源，为了整个社区的良性发展，我们**强烈建议**您做到以下几点：

- **间接接触（包括但不限于使用 `Http API` 或 跨进程技术）到 `mirai` 的软件使用 `AGPLv3` 开源**
- **不鼓励，不支持一切商业使用**

鉴于项目的特殊性，开发团队同样可能在任何时间**停止更新**或**删除项目**，本项目在孵化阶段为本团队内部使用项目，经过诸多测试和实践已经相对稳定，现已孵化结束进入正式更新版本。

## 使用问题

使用本项目遇到任何问题，首先判断是属于Mirai本身问题还是Starter处理问题：

* 有关本Starter整合相关问题：可以在 [issues](https://github.com/itbaima-study/itbaima-robot-starter/issues) 中提出。

* 如果对Mirai提供的API本身使用有任何疑问，可以在其官方站点查询：

  * **用户手册**: [UserManual](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)

    > 如果你希望快速部署一个 Mirai QQ 机器人，安装插件、并投入使用，请看这里

  * 论坛: [Mirai Forum](https://mirai.mamoe.net/)

    > Mirai 只有**唯一一个**官方论坛 Mirai Forum

  * 在线讨论: [Gitter](https://gitter.im/mamoe/mirai?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

## 快速上手

在开始之前，请根据我们的指引完成对机器人相关的配置：https://github.com/itbaima-study/itbaima-robot-starter/wiki 当一切配置无误时，就可以开始体验了。

我们还准备了B站视频教程：https://www.bilibili.com/video/BV1Rp4y1J7kh/

### 使用监听器

你可以使用注解快速编写监听器，监听即将到来的事件：

```java
@RobotListener   //添加@RobotListener注解表示这是一个监听器类
public class MyListener {

    @RobotListenerHandler  //监听器类中可以有很多处理器方法，用于处理对应的事件
    public void handleJoin(MemberJoinRequestEvent event) {  //处理方法中需要填写事件类型对应的参数
        //这里处理的是MemberJoinRequestEvent新成员加入事件
    }
}
```

有了监听器，我们就可以快速处理事件了。比如我们想要实现在群成员发违禁词的情况下对其进行禁言操作，那么我们可以向下面这样编写：

```java
@RobotListener
public class NormalGroupListener extends MessageListener {
		@RobotListenerHandler
    public void handleMessage(GroupMessageEvent event) {
        String message = event.getMessage().contentToString();
        if(message.contains("傻逼")) {   //检测到违禁词
            Member sender = event.getSender();  //获取到消息发送者
            sender.mute(60);  //直接禁言60秒
        }
    }
}
```

当然，可能我们的QQ机器人加了很多个群，那么此时我们需要对监听的群进行限制，只处理我们指定群号的事件，比如下面这种情况：

```java
@RobotListener
public class NormalGroupListener extends MessageListener {
		@RobotListenerHandler(contactId = {123456789})  //只监听123456789这个群的消息
    public void handleMessage(GroupMessageEvent event) {
        //注意这种方式仅适用于那些与用户或群这类可以获取ID的事件
    }
}
```

有些时候可能会存在很多个监听器，或是很多个相同的事件处理方法，我们可以对其进行排序操作：

```java
@RobotListener
public class NormalGroupListener extends MessageListener {
		@RobotListenerHandler(order = 1) //order数值越小越优先，默认为0
    public void handleMessage(GroupMessageEvent event) {
        //此处理器优先进行
    }
  
    @RobotListenerHandler(order = 5)
    public void handleMessage(GroupMessageEvent event) {  //同样是GroupMessageEvent的事件处理器
        //此处理器后进行
    }
}
```

正常情况下，所有的事件处理器都是按照顺序进行的，但是可能有些时候我们为了效率，希望事件处理器并发执行，我们可以直接配置：

```java
public class NormalGroupListener extends MessageListener {
		@RobotListenerHandler(order = 1) 
    public void handleMessage(GroupMessageEvent event) {
      	Thread.sleep(3000);  //这里的阻塞不会影响到并发执行的其他事件处理器
    }
  
    @RobotListenerHandler(order = 5, concurrency = true) //开启并发执行，无视顺序直接新开线程处理
    public void handleMessage(GroupMessageEvent event) {
        //此处理器与上面的处理器并发执行，不受影响
    }
}
```

有了监听器，我们对于Mirai中一些常见的事件可以很方便地进行处理了。

### 使用MessageListener类

考虑到各位小伙伴可能会在群里做一些常用的操作，比如判断违禁词之类的，我们封装了一个 MessageListener 类用于继承，它提供了大量的预设操作：

```java
@RobotListener
public class TestListener extends MessageListener {

    public TestListener(){
        //构造时配置（也可以延迟设置）违禁词列表，并设置大小写敏感
        super(List.of("傻逼", "弱智", "脑残"), true);
    }
    
    @RobotListenerHandler
    public void handleMessage(GroupMessageEvent event){
        String s = event.getMessage().contentToString();
        if(this.invalidText(s)) {  //直接使用预设的 invalidText 判断是否出现违禁词
            System.out.println("检测到违禁词");
        }
    }
}
```

除了违禁词检查，还支持撤回消息、快速回复某条消息、快速AT某个成员等，还请各位小伙伴自行阅读源码。

### 自动注册的Bean

在使用Starter后，一些对象会被自动注册为Bean，比如Mirai的机器人对象、IMirai对象等：

```java
@RobotListener
public class NormalGroupListener extends MessageListener {

    @Resource
    Bot bot;   //可以直接注入

    @Resource
    IMirai mirai;  //可以直接注入
}
```

我们也提供了一个专用于机器人操作的 Service 类用于处理各种常规操作：

```java
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
```

它同样包含了大量日常机器人操作的方法，RobotService同样被自动注册为Bean，并且可以随时使用。

## 相关项目
* https://github.com/cssxsh/fix-protocol-version 版本协议临时修复插件
* https://github.com/fuqiuluo/unidbg-fetch-qsign fuqiuluo签名服务器
* https://github.com/kiliokuara/magic-signer-guide kiliokuara签名服务器
* https://github.com/mamoe/mirai Mirai核心
