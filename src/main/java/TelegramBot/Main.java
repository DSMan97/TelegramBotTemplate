package TelegramBot;


import TelegramBot.Controller.TelegramBot;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;


public class Main{

    public static void main(String[] args) {
        PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties"));

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotApi = new TelegramBotsApi();
        final TelegramBot telegramBot = new TelegramBot();

        try {
            telegramBotApi.registerBot(telegramBot);

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }





    }


}