package TelegramBot.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties PROPS = new Properties();
    private static final String PROPERTIES_PATH = "application.properties";

    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(PROPERTIES_PATH);
        try {
            PROPS.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Config()  {

    }

    public static String getString(String key) {
        return PROPS.getProperty(key);
    }


}
