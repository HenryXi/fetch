package com.henry.util;

import java.io.*;
import java.util.Properties;

public class Config {
    private static Config config;
    private static Properties prop;

    private Config(String configFileName) {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName);
            prop = new Properties();
            prop.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Config getInstance(String configFileName) {
        if (config == null) {
            return new Config(configFileName);
        }
        return config;
    }

    public static String getString(String propertyName) {
        return prop.getProperty(propertyName);
    }

    public static int getInt(String propertyName) {
        return Integer.valueOf(prop.getProperty(propertyName));
    }

    public static void saveProperty(String key, String value) {
        prop.setProperty(key, value);
        File f = new File("fetch.properties");
        OutputStream out = null;
        try {
            f.createNewFile();
            out = new FileOutputStream(f);
            prop.store(out, "This is an optional header comment string");
            System.out.println("save current index in new properties file. value is: " + value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    System.out.println("IOException: Could not close myApp.properties output stream; " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }
}
