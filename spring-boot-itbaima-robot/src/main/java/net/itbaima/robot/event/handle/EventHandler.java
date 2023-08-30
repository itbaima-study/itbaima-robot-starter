package net.itbaima.robot.event.handle;


import net.itbaima.robot.event.RobotListenerHandler;
import net.mamoe.mirai.event.Event;

import java.util.function.Consumer;

/**
 * 事件处理器
 * @param annotation 注解
 * @param consumer 事件消费函数
 */
public record EventHandler(RobotListenerHandler annotation, Consumer<Event> consumer) {
    public void accept(Event event) {
        consumer.accept(event);
    }

    public void acceptIfContainsId(long id, Event event) {
        if(annotation.contactId().length == 0) {
            consumer.accept(event);
        } else {
            boolean contains = false;
            for (long l : annotation.contactId()) {
                if(l == id) {
                    contains = true;
                    break;
                }
            }
            if(contains) consumer.accept(event);
        }
    }

    public int compareOrder(EventHandler another){
        return Integer.compare(this.annotation.order(), another.annotation.order());
    }
}
