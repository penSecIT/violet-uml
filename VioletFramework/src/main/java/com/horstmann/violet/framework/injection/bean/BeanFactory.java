package com.horstmann.violet.framework.injection.bean;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.horstmann.violet.framework.injection.bean.annotation.InjectableBean;

public class BeanFactory {

    /**
     * Singleton constructor
     */
    private BeanFactory() {
        // Singleton pattern
    }

    /**
     * @return the only object instance
     */
    public static BeanFactory getFactory() {
        if (BeanFactory.instance == null) {
            BeanFactory.instance = new BeanFactory();
        }
        return BeanFactory.instance;
    }

    /**
     * Looks for a bean from its type
     * 
     * @param <T>
     * @param classType
     * @return
     */
    public <T> T getBean(Class<T> classType) {
        if (singletonsMap.containsKey(classType)) {
            Object object = singletonsMap.get(classType);
            Class<? extends Object> implementationType = object.getClass();
            InjectableBean annotation = implementationType.getAnnotation(InjectableBean.class);
            if (annotation == null) {
                throw new RuntimeException("Error on " + implementationType.getName() + " . All the class instances you want to inject must have the annotation @" + InjectableBean.class.getSimpleName() + " declared.");
            }
        }
        if (!singletonsMap.containsKey(classType)) {
            createBean(classType);
        }
        return (T) singletonsMap.get(classType);
    }

    /**
     * Allows to a bean register manually. If this bean has unset @InjectedBean
     * field, they would be automatically injected.
     * 
     * @param classType
     *            (prefer using the interface here)
     * @param aBean
     */
    public void register(Class<?> classType, Object implementation) {
        singletonsMap.put(classType, implementation);
        BeanInjector beanInjector = BeanInjector.getInjector();
        try {
            beanInjector.inject(implementation);
        } catch (RuntimeException re) {
            throw new RuntimeException("Error while registering a bean of type " + classType.getName() + " . Perhaps you register another bean before this one." , re);
        }
    }

    /**
     * Creates a new class instance
     * 
     * @param <T>
     * @param classType
     */
    private <T> void createBean(Class<T> classType) {
        InjectableBean annotation = classType.getAnnotation(InjectableBean.class);
        if (annotation == null) {
            throw new RuntimeException("Error on " + classType + " . All the class instances you want to inject must have the annotation @" + InjectableBean.class.getSimpleName() + " declared.");
        }
        if (!annotation.autoCreate()) {
            throw new RuntimeException("BeanFactory cannot create bean instance of type " + classType.getName() + " because you set the flag autocreate to false");
        }
        boolean isEmptyContructor = isEmptyConstructor(classType);
        if (!isEmptyContructor) {
            throw new RuntimeException("BeanFactory cannot create instance of type " + classType.getName() + " because it doesn't have a public constructor with no parameter.");
        }
        try {
            T newInstance = classType.newInstance();
            BeanInjector beanInjector = BeanInjector.getInjector();
            beanInjector.inject(newInstance);
            singletonsMap.put(classType, newInstance);
        } catch (Exception e) {
            throw new RuntimeException("BeanFactory failed to create bean of type " + classType.getName(), e);
        }
    }

    /**
     * @param classType
     * @return true if the class has at least one empty constructor
     */
    private boolean isEmptyConstructor(Class<?> classType) {
        boolean isEmptyContructor = false;
        Constructor<?>[] constructors = classType.getConstructors();
        for (Constructor<?> aConstructor : constructors) {
            Class<?>[] parameterTypes = aConstructor.getParameterTypes();
            if (parameterTypes.length == 0) {
                isEmptyContructor = true;
            }
        }
        return isEmptyContructor;
    }

    private Map<Class<?>, Object> singletonsMap = new HashMap<Class<?>, Object>();

    /**
     * Singleton instance
     */
    private static BeanFactory instance;

}
