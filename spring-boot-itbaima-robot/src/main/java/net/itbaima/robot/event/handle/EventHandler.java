package net.itbaima.robot.event.handle;


import net.mamoe.mirai.event.Event;

import java.util.function.Consumer;

/**
 * 事件处理器
 * @param order 顺序
 * @param concurrency 是否并发
 * @param consumer 事件消费函数
 */
public record EventHandler(int order, boolean concurrency, Consumer<Event> consumer) {
    public void accept(Event event) {
        consumer.accept(event);
    }

    public int compareOrder(EventHandler another){
        return this.order - another.order;
    }
}
