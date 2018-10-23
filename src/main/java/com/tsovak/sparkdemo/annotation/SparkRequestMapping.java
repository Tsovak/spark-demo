package com.tsovak.sparkdemo.annotation;

import com.tsovak.sparkdemo.transformer.BaseTransformer;
import com.tsovak.sparkdemo.transformer.JsonTransformer;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SparkRequestMapping {

    /**
     * The path mapping URIs
     */
    String path();

    /**
     * The HTTP request methods to map to, narrowing the primary mapping:
     * GET, POST, OPTIONS, PUT, PATCH, DELETE.
     */
    RequestMethod method();

    Class<? extends BaseTransformer> transformer() default JsonTransformer.class ;

}
