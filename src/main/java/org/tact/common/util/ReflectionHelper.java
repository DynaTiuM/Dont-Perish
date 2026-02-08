package org.tact.common.util;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionHelper {
    private static final Map<String, Method> METHOD_CACHE = new HashMap<>();


    public static <T> T invokeMethod(Object target, String methodName, Object... args) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }

        Class<?> targetClass = target.getClass();
        String cacheKey = buildCacheKey(targetClass, methodName);

        Method method = METHOD_CACHE.get(cacheKey);
        if (method == null) {
            method = findMethod(targetClass, methodName, getArgumentTypes(args));
            method.setAccessible(true);
            METHOD_CACHE.put(cacheKey, method);
        }

        try {
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(target, args);
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(
                    "Failed to invoke method: " + methodName + " on " + targetClass.getName(),
                    e
            );
        }
    }

    private static Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredMethod(methodName, argTypes);
            } catch (NoSuchMethodException e) {
                currentClass = currentClass.getSuperclass();
            }
        }

        throw new RuntimeException(
                "Method not found: " + methodName + " in " + clazz.getName()
        );
    }

    private static String buildCacheKey(Class<?> clazz, String methodName) {
        return clazz.getName() + "#" + methodName;
    }


    private static Class<?>[] getArgumentTypes(Object... args) {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = getArgumentType(args[i]);
        }
        return types;
    }

    private static Class<?> getArgumentType(Object arg) {
        if (arg == null) {
            return Object.class;
        }

        return switch (arg) {
            case Integer i -> Integer.TYPE;
            case Long l -> Long.TYPE;
            case Boolean b -> Boolean.TYPE;
            case Double d -> Double.TYPE;
            case Float f -> Float.TYPE;
            case Byte by -> Byte.TYPE;
            case Short s -> Short.TYPE;
            case Character c -> Character.TYPE;
            default -> arg.getClass();
        };
    }
}
