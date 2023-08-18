package net.itbaima.robot.event.handle;

import net.itbaima.robot.event.RobotListenerHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 解析所有监听器中的处理器方法，并统一完成通道订阅操作
 */
public class HandlerResolver {
    private final Object bean;
    private final BeanFactory factory;
    private final Method[] declaredMethods;
    private static final Map<Class<? extends Event>, PriorityQueue<EventHandler>> handlers = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(HandlerResolver.class);

    public HandlerResolver(Object bean, BeanFactory factory, Method... declaredMethods){
        this.bean = bean;
        this.factory = factory;
        this.declaredMethods = declaredMethods;
        this.resolve();
    }

    public Set<Class<? extends Event>> events() {
        return handlers.keySet();
    }

    public static void subscribe(Bot bot) {
        bot.getEventChannel().subscribeAlways(Event.class, event -> handlers.keySet().forEach(clazz -> {
            if(event.getClass().isAssignableFrom(clazz)) {
                handlers.get(clazz).forEach(handler -> {
                    if(handler.concurrency()) {
                        Thread thread = new Thread(() -> handler.accept(event), "robot-handler-" + System.currentTimeMillis());
                        thread.start();
                    } else {
                        handler.accept(event);
                    }
                });
            }
        }));
    }

    private void resolve(){
        for (Method method : declaredMethods) {
            RobotListenerHandler annotation = method.getAnnotation(RobotListenerHandler.class);
            if(annotation == null) continue;
            Class<? extends Event> event = this.methodEvent(method);
            this.addHandlerMethod(annotation.order(), annotation.concurrency(), event, method);
        }
    }

    private Class<? extends Event> methodEvent(Method method) {
        Parameter[] parameters = method.getParameters();
        if(parameters.length < 1 || parameters[0].getType().isAssignableFrom(Event.class))
            throw new IllegalArgumentException("监听器Handler方法的第一个参数必须是Event及其子类型!");
        return parameters[0].getType().asSubclass(Event.class);
    }

    private void addHandlerMethod(int order, boolean concurrency, Class<? extends Event> eventClazz, Method method) {
        Consumer<Event> invoke = event -> this.invokeMethod(method, event);
        if (!handlers.containsKey(eventClazz))
            handlers.put(eventClazz, new PriorityQueue<>(EventHandler::compareOrder));
        handlers.get(eventClazz).offer(new EventHandler(order, concurrency, invoke));
    }

    private void invokeMethod(Method method, Event event) {
        Object[] paramObjs = new Object[method.getParameterCount()];
        paramObjs[0] = event;
        Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            Object object = factory.getBean(parameters[i].getType());
            paramObjs[i - 1] = object;
        }
        try {
            method.invoke(bean, paramObjs);
        }catch (ReflectiveOperationException exception) {
            logger.error("执行机器人事件监听器Handler时出错，当前监听器: " + bean, exception);
        }
    }
}
