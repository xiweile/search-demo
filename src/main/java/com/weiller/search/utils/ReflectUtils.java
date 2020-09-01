package com.weiller.search.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectUtils
{
  public static <T> T createInstance(Class<?> clazz)
  {
    try
    {
      if (clazz == null)
        return null;
 
      return (T) clazz.newInstance();
    }
    catch (Exception e) {
      throw new RuntimeException("Error occured during creating instance of " + clazz, e);
    }
  }
 
  public static Object getField(Field field, Object instance) throws RuntimeException {
    try {
      if (!(field.isAccessible())) {
        field.setAccessible(true);
      }
 
      return field.get(instance);
    } catch (Exception e) {
      throw new RuntimeException("Error occured during getting field: " + field, e.getCause());
    }
  }
 
  public static Object invokeGetter(Object instance, String property) {
    Method getter;
    Class clazz = instance.getClass();
    try
    {
      getter = clazz.getMethod("get" + Character.toUpperCase(property.charAt(0)) + property.substring(1), new Class[0]);
    } catch (Exception e) {
      throw new RuntimeException("No getter method found: " + e, e);
    }
 
    return invokeMethod(getter, instance, new Object[0]);
  }
 
  public static Object invokeMethod(Method method, Object instance, Object[] parameters) throws RuntimeException {
    try {
      return method.invoke(instance, parameters);
    }
    catch (Exception e) {
      throw new RuntimeException("Error occured during invoking method: " + method + " with parameters(" + 
        Arrays.asList(parameters)
         + ")", e.getCause());
    }
  }
 
  public static void setField(Field field, Object instance, Object value) throws RuntimeException {
    try {
      if (!(field.isAccessible())) {
        field.setAccessible(true);
      }
 
      field.set(instance, value);
    }
    catch (Exception e) {
      throw new RuntimeException("Error occured during setting field: " + field + " with value(" + value + ")", e
        .getCause());
    }
  }
}