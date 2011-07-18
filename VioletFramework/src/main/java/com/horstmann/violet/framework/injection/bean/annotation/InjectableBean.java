package com.horstmann.violet.framework.injection.bean.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to auto create beans with the BeanFactory
 * 
 * @author Alexandre de Pellegrin
 * 
 */
@Target( { TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface InjectableBean {

    /**
     * @return true if the bean instance is automatically created by the BeanFactory or registered manually 
     */
    public boolean autoCreate() default true;

}
