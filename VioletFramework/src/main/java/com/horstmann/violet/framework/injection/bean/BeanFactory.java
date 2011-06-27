package com.horstmann.violet.framework.injection.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import com.horstmann.violet.framework.injection.bean.annotation.ManagedBean;

public class BeanFactory {

    /**
     * Default constructor. NOT PART OF THE PUBLIC API.
     */
    public BeanFactory() {
        instance = this;
    }
    
    private void init() {
        
        
    }
 
    /**
     * @return the only object instance
     */
    public static BeanFactory getFactory() {
        return instance;
    }



    /**
     * Looks for a bean from its type
     * 
     * @param <T>
     * @param classType
     * @return
     */
    public <T> T getBean(Class<T> classType) {
        ManagedBean managedBeanAnnotation = classType.getAnnotation(ManagedBean.class);
        if (managedBeanAnnotation == null) {
            throw new RuntimeException("Bean of type " + classType.getName() + " not managed by the BeanFactory");
        }
        boolean isEmptyContructor = false;
        Constructor<?>[] constructors = classType.getConstructors();
        for (Constructor<?> aConstructor : constructors) {
            Class<?>[] parameterTypes = aConstructor.getParameterTypes();
            if (parameterTypes.length == 0) {
                isEmptyContructor = true;
            }
        }
        if (!isEmptyContructor) {
            throw new RuntimeException("BeanFactory cannot create instance of type " + classType.getName() + " because it doesn't have a constructor without any parameter");
        }
        if (!singletonsMap.containsKey(classType)) {
            try {
                T newInstance = classType.newInstance();
                BeanInjector beanInjector = BeanInjector.getInjector();
                beanInjector.inject(newInstance);
                singletonsMap.put(classType, newInstance);
            } catch (Exception e) {
                throw new RuntimeException("BeanFactory failed to create bean of type " + classType.getName() + " for the following reason : " + e.getMessage());
            }
        }
        return (T) singletonsMap.get(classType);
    }

    private Map<Class<?>, Object> singletonsMap = new HashMap<Class<?>, Object>();

    /**
     * Singleton instance
     */
    private static BeanFactory instance;

}
