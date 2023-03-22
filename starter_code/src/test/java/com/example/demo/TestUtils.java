package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class TestUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    public static void injectObjects(Object target, String fieldName, Object objToInject){

        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            if(!field.canAccess(target)){
                field.setAccessible(true);
                wasPrivate = true;
            }
            field.set(target, objToInject);
            if(wasPrivate){
                field.setAccessible(false);
            }

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
