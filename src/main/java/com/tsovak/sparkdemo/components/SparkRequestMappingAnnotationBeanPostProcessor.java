package com.tsovak.sparkdemo.components;

import com.tsovak.sparkdemo.annotation.SparkRequestMapping;
import com.tsovak.sparkdemo.transformer.BaseTransformer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import spark.Route;
import spark.Spark;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class SparkRequestMappingAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(SparkRequestMapping.class)) {
                SparkRequestMapping annotation = method.getAnnotation(SparkRequestMapping.class);
                method.setAccessible(true);
                String path = annotation.path();
                Route route = invoke(method, bean);

                Spark.staticFiles.header("Access-Control-Allow-Origin", "*");
                BaseTransformer transformer = null;
                try {
                    transformer = annotation.transformer().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("The Transformer should have a empty constructor", e);
                }
                String acceptType = transformer.getAcceptType();
                switch (annotation.method()) {
                    case GET:
                        Spark.get(path, acceptType, route, transformer);
                        if (!path.endsWith("/")) {
                            Spark.get(path.concat("/"), acceptType, route, transformer);
                        }
                        break;
                    case POST:
                        Spark.post(path, acceptType, route, transformer);
                        if (!path.endsWith("/")) {
                            Spark.post(path.concat("/"), acceptType, route, transformer);
                        }
                        break;
                    case DELETE:
                        Spark.delete(path, acceptType, route, transformer);
                        if (!path.endsWith("/")) {
                            Spark.delete(path.concat("/"), acceptType, route, transformer);
                        }
                        break;
                    case PUT:
                        Spark.put(path, acceptType, route, transformer);
                        if (!path.endsWith("/")) {
                            Spark.put(path.concat("/"), acceptType, route, transformer);
                        }
                        break;
                    case PATCH:
                        Spark.patch(path, acceptType, route, transformer);
                        if (!path.endsWith("/")) {
                            Spark.patch(path.concat("/"), acceptType, route, transformer);
                        }
                        break;
                    case OPTIONS:
                        Spark.options(path, route, transformer);
                        break;
                    default:
                        throw new UnsupportedOperationException(String.format("The SparkRequestMapping %s method not supported.", annotation.method().name()));
                }
            }
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    private Route invoke(Method method, Object bean) {
        try {
            return (Route) method.invoke(bean, new Object[]{});
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
