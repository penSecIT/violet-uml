package com.horstmann.violet.framework.injection.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.horstmann.violet.framework.injection.bean.annotation.InjectedBean;

/**
 * This injector is made to perform dependency injection on objects. This class
 * was inspired from the excellent Apache Wicket framework.
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class BeanInjector {

    /**
     * Singleton constructor
     */
    public BeanInjector() {
        // Singleton pattern
    }

    /**
     * @return the only object instance
     */
    public static BeanInjector getInjector() {
        if (BeanInjector.instance == null)
        {
            BeanInjector.instance = new BeanInjector();
        }
        return BeanInjector.instance;
    }

    /**
     * Injects Spring beans on fields annotated with \@SpringBean
     * 
     * @param o
     */
    public void inject(Object o) {
        List<Class<?>> classAndSuperClasses = getClassAndSuperClasses(o);
        for (Class<?> aClass : classAndSuperClasses) {
            // Injects on fields (only if they haven't any value set)
            for (Field aField : aClass.getDeclaredFields()) {
                InjectedBean propertyAnnotation = aField.getAnnotation(InjectedBean.class);
                if (propertyAnnotation != null) {
                    aField.setAccessible(true);
                    if (!isFieldNUll(aField, o)) {
                        continue;
                    }
                    Class<?> implementationClass = propertyAnnotation.implementation();
                    if (implementationClass.equals(Object.class)) {
                        implementationClass = aField.getType();
                    }
                    Object beanToInject = BeanFactory.getFactory().getBean(implementationClass);
                    if (beanToInject == null) {
                        throw new RuntimeException("Unable to inject a bean of type " + implementationClass.getName() + " . No such bean found.");
                    }
                    try {
                        aField.set(o, beanToInject);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Error while setting field value of bean managed by the BeanFactory", e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error while setting field value of bean managed by the BeanFactory", e);
                    }
                }
            }
        }
    }

    /**
     * Takes an objet and returns its class and all its inherited classes
     * 
     * @param o
     * @return
     */
    private List<Class<?>> getClassAndSuperClasses(Object o) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        List<Class<?>> fifo = new ArrayList<Class<?>>();
        fifo.add(o.getClass());
        while (!fifo.isEmpty()) {
            Class<?> aClass = fifo.remove(0);
            ;
            Class<?> aSuperClass = aClass.getSuperclass();
            if (aSuperClass != null) {
                fifo.add(aSuperClass);
            }
            result.add(aClass);
        }
        return result;
    }

    /**
     * Checks if a field is empty (null value)
     * 
     * @param aField
     * @param o
     * @return true if null
     */
    private boolean isFieldNUll(Field aField, Object o) {
        try {
            Object currentValue = aField.get(o);
            if (currentValue == null) {
                return true;
            }
        } catch (IllegalArgumentException e1) {
            throw new RuntimeException("Error while getting field value of bean managed by the BeanFactory. Field = " + aField.getName(), e1);
        } catch (IllegalAccessException e1) {
            throw new RuntimeException("Error while getting field value of bean managed by the BeanFactory. Field = " + aField.getName(), e1);
        }
        return false;
    }

    /**
     * Singleton instance
     */
    private static BeanInjector instance;
}
