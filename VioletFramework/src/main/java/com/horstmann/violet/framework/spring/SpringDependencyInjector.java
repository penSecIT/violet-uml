package com.horstmann.violet.framework.spring;

import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.horstmann.violet.framework.spring.annotation.SpringBean;


/**
 * This injector is made to perform Spring dependency injection on objects which are not managed by Spring.
 * This class was inspired from the excellent Apache Wicket framework. 
 * 
 * @author Alexandre de Pellegrin
 *
 */
public class SpringDependencyInjector implements ApplicationContextAware
{



    /**
     * Spring constructor. NOT PART OF THE PUBLIC API.
     */
    public SpringDependencyInjector() {
        instance = this;
    }
    
    /**
     * @return the only object instance
     */
    public static SpringDependencyInjector getInjector() {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.context = context;
    }


    /**
     * Injects Spring beans on fields annotated with \@SpringBean 
     * @param o
     */
    public void inject(Object o) {
        // Injects on fields
        for (Field aField : o.getClass().getDeclaredFields())
        {
            SpringBean propertyAnnotation = aField.getAnnotation(SpringBean.class);
            if (propertyAnnotation != null) {
                String beanName = propertyAnnotation.name();
                if ("".equals(beanName)) {
                    beanName = aField.getName();
                }
                Object beanToInject = this.context.getBean(beanName);
                if (beanToInject == null) {
                    continue;
                }
                aField.setAccessible(true);
                try
                {
                    aField.set(o, beanToInject);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException("Error while injecting bean maanged by Spring", e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException("Error while injecting bean managed by Spring", e);
                }
            }
        }
    }
    
    /**
     * Spring context
     */
    private ApplicationContext context;
    
    /**
     * Singleton instance
     */
    private static SpringDependencyInjector instance;
}
