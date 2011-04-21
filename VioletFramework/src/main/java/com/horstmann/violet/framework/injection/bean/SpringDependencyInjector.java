package com.horstmann.violet.framework.injection.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.horstmann.violet.framework.injection.bean.annotation.SpringBean;


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
        List<Class<?>> classAndSuperClasses = getClassAndSuperClasses(o);
        for (Class<?> aClass : classAndSuperClasses) {
            // Injects on fields
            for (Field aField : aClass.getDeclaredFields())
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
    }
    
    /**
     * Takes an objet and returns its class and all its inherited classes 
     * @param o
     * @return
     */
    private List<Class<?>> getClassAndSuperClasses(Object o) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        List<Class<?>> fifo = new ArrayList<Class<?>>();
        fifo.add(o.getClass());
        while (!fifo.isEmpty()) {
            Class<?> aClass = fifo.remove(0);;
            Class<?> aSuperClass = aClass.getSuperclass();
            if (aSuperClass != null) {
                fifo.add(aSuperClass);
            }
            result.add(aClass);
        }
        return result;
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
