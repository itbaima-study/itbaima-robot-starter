package net.itbaima.robot.event;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import net.itbaima.robot.event.handle.HandlerResolver;
import net.mamoe.mirai.Bot;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Method;

/**
 * 处理所有机器人事件监听器的后置处理器
 */
public class RobotEventPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    @Resource
    private Bot bot;

    private final Logger logger = LoggerFactory.getLogger(RobotEventPostProcessor.class);

    private ConfigurableListableBeanFactory factory;

    @PostConstruct
    public void init() {
        HandlerResolver.subscribe(bot);
    }

    @Override
    public void setBeanFactory(@NotNull BeanFactory factory) throws BeansException {
        this.factory = (ConfigurableListableBeanFactory) factory;
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if(factory.getBeanDefinition(beanName) instanceof AnnotatedBeanDefinition definition) {
            AnnotationMetadata metadata = definition.getMetadata();
            if(metadata.hasAnnotation(RobotListener.class.getName())) {
                Class<?> beanClass = bean.getClass();
                Method[] declaredMethods = beanClass.getDeclaredMethods();
                HandlerResolver resolver = new HandlerResolver(bean, factory, declaredMethods);
                logger.info("Register robot listener bean: {}, successfully, the listener will listen events {}",
                        bean.getClass(),
                        resolver.events());
            }
        }
        return bean;
    }
}
