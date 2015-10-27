package com.henry.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Config config;
    private static Properties prop;
    private Config(){
        InputStream inputStream=null;
        try {
            inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("fetch.properties");
            prop = new Properties();
            prop.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static Config getInstance(){
        if(config==null){
            return new Config();
        }
        return config;
    }

    public static String getString(String propertyName) {
        return prop.getProperty(propertyName);
    }

    public static int getInt(String propertyName) {
        return Integer.valueOf(prop.getProperty(propertyName));
    }
}
