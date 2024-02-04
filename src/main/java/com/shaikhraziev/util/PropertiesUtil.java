package com.shaikhraziev.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Properties;

/**
 * Позволяет считать значения из файла application.properties
 */
@UtilityClass
public class PropertiesUtil {
    /**
     * Поле, для работы с properties
     */
    private static final Properties PROPERTIES = new Properties();

    static {
        try (var inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получение property по ключу
     * @param key   Ключ
     * @return      Возвращает значение по ключу
     */
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}