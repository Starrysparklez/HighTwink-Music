package net.hightwink.musicbot.classes.utils;

public class ArrayUtil {
    public static <T> boolean isPresent(T[] array, T object) {
        for (T obj : array)
            if (obj.equals(object)) return true;
        return false;
    }
}
