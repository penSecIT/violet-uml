package com.horstmann.violet.framework.injection.bean.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to inject beans in objects which are not managed by Spring  
 * 
 * @author Alexandre de Pellegrin
 *
 */
@Target({FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface InjectedBean
{

    /**
     * @return the desired implementation. Optional. If not precised, it takes the field's type
     */
    public Class<?> implementation() default Object.class;
    
}
