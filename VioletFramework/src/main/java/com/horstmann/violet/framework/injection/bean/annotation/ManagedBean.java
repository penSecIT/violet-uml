package com.horstmann.violet.framework.injection.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to auto create beans with the BeanFactory  
 * 
 * @author Alexandre de Pellegrin
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ManagedBean
{

    public String name() default "";
    
}
