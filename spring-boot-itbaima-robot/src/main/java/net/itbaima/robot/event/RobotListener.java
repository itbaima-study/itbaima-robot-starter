package net.itbaima.robot.event;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 添加此注解的类都是消息监听器
 *
 * <p>消息监听器可以对各种各样的事情进行监听，比如群里有成员发送消息、
 * 好友发送消息、有用户申请加群以及各种各样可能会发生的事情，使用监听器
 * 就可以实现消息快速监听。
 *
 * <p>每一个监听器内都需要对应的事件处理器，事件处理器以方法形式表示，
 * 每一个事件处理器都可以处理一个事件，只需要将对应的事件 {@link net.mamoe.mirai.event.Event}
 * 添加到方法中作为参数，最后在对应的方法上添加 {@link RobotListenerHandler}
 * 即可，在发生对应事件时，会自动调用一次此方法。
 *
 * <p>每一个监听器都会自动被扫描并注册为Bean进行管理，所以不需要配置
 * 其他的任何内容，比如包扫描之类的东西。
 *
 * @since 1.0.0
 * @author Ketuer
 * @see net.itbaima.robot.event.RobotListenerHandler
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RobotListener {
}
