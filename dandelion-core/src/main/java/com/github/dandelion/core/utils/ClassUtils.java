package com.github.dandelion.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Utility methods for dealing with classes.
 */
public class ClassUtils {
    /**
     * Prevents instantiation.
     */
    private ClassUtils() {
        // Do nothing
    }

    /**
     * Creates a new instance of this class.
     *
     * @param className The fully qualified name of the class to instantiate.
     * @param <T>       The type of the new instance.
     * @return The new instance.
     * @throws Exception Thrown when the instantiation failed.
     */
    @SuppressWarnings({"unchecked"})
    // Must be synchronized for the Maven Parallel Junit runner to work
    public static synchronized <T> T instantiate(String className) throws Exception {
        return (T) Class.forName(className, true, getClassLoader()).newInstance();
    }

    /**
     * Checks whether this class can be instantiated or not (abstract, no default constructor, ...).
     *
     * @param clazz The class to check.
     * @return {@code true} if it can, {@code false} if it can't.
     */
    public static boolean canInstantiate(Class<?> clazz) {
        try {
            clazz.newInstance();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return The classloader to use for loading classes.
     */
    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Determine whether the {@link Class} identified by the supplied name is present
     * and can be loaded. Will return {@code false} if either the class or
     * one of its dependencies is not present or cannot be loaded.
     *
     * @param className the name of the class to check
     * @return whether the specified class is present
     */
    public static boolean isPresent(String className) {
        try {
            getClassLoader().loadClass(className);
            return true;
        } catch (Throwable ex) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }

    /**
     * Computes the short name (name without package) of this class.
     *
     * @param aClass The class to analyse.
     * @return The short name.
     */
    public static String getShortName(Class<?> aClass) {
        String name = aClass.getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    /**
     * Retrieves the physical location on disk of this class.
     *
     * @param aClass The class to get the location for.
     * @return The absolute path of the .class file.
     */
    public static String getLocationOnDisk(Class<?> aClass) {
        try {
            String url = aClass.getProtectionDomain().getCodeSource().getLocation().getPath();
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //Can never happen.
            return null;
        }
    }
}
