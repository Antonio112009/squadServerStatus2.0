package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class Config {
    public static String getProperty(String propertyName){
        try (FileInputStream fileInput = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(new InputStreamReader(fileInput, Charset.forName("UTF-8")));
            return prop.getProperty(propertyName);
        } catch (IOException ex) {
            return null;
        }
    }
}
