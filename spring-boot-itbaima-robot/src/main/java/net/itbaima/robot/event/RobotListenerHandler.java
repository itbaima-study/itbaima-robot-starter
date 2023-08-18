package net.itbaima.robot.event;

import java.lang.annotation.*;

/**
 * 请在需要作为监听器对应Handler的方法上添加此注解
 *
 * <p>监听器类需要添加 {@link RobotListener} 注解表示，监听器类在加载时会自动将其中
 * 所有添加了 {@link RobotListenerHandler} 注解的方法作为对应事件发生时的回调函数，
 * 对于同一个事件可以注册多个Handler方法，顺序默认为监听器类的的扫描顺序，这是不确定的，
 * 因此可以使用 {@code order} 来手动指定优先级。
 *
 * <p>注意，对于Handler处理的优先级，仅在当前监听器内部生效，如果其他监听中同样存在监听此事件
 * 的Handler方法，那么会根据监听器的优先级决定，同时，如果Handler配置了并发执行，那么将直接无
 * 视此优先级。
 *
 * <p>作为监听器Handler的方法，其首个形参必须为 {@link net.mamoe.mirai.event.Event} 的子类，
 * 后续形参会自动从Spring提供的IoC容器中去寻找，如果找不到会抛出异常。每一个Handler有且只能有
 * 一个 {@link net.mamoe.mirai.event.Event} 及其子类类型的参数，就像下面这样:
 *
 * <p>对于一些特殊的事件，比如好友或群聊相关的事件，对应的类型为 {@link net.mamoe.mirai.event.events.GroupEvent}
 * 和 {@link net.mamoe.mirai.event.events.FriendEvent} 这些类型一般包含群号或QQ号，
 * 我们可以配置 {@code contactId} 属性从而只监听对应群号的消息，默认情况下监听所有的群号或QQ号消息。
 *
 * <p>一个标准的Handler方法如下:
 * <pre class="code">
 * &#064;RobotListenerHandler(order = 1, contactId = 12345678)
 * public void handleGroupMessage(GroupMessageEvent event, ...) {
 *     System.out.println(event.getMessage());
 * }
 * </pre>
 *
 * @since 1.0.0
 * @author Ketuer
 * @see #order()
 * @see #contactId()
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RobotListenerHandler {

    /**
     * 手动配置当前监听器内Handler方法的优先级，默认情况下为0，按照扫描顺序排列
     * @return 优先级
     */
    int order() default 0;

    /**
     * 对于一些特殊类型，可以配置只监听指定的Id发生的事件
     * @return 监听Id列表
     */
    long[] contactId() default {};

    /**
     * 是否并发执行，无需等待其他Handler完成，当事件到来时直接并发执行
     * @return 是否并发执行
     */
    boolean concurrency() default false;
}
