package com.horstmann.violet.framework.spring.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to inject beans in objects which are not managed by Spring  
 * 
 * @author Alexandre de Pellegrin
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SpringBean
{

    public String name() default "";
    
}
