package TelegramBot.commands.impl;

import TelegramBot.Controller.TelegramBot;
import TelegramBot.Menus.Menus;
import TelegramBot.commands.CommandExecutor;
import TelegramBot.commands.annotation.Command;
import org.telegram.telegrambots.meta.api.objects.Update;

@Command("help")
public class HelpCommandExecutor implements CommandExecutor {
    Menus menus = new Menus();

    @Override
    public void execute(TelegramBot bot, int replyMessageId, Update update, String... args) {
        String ownerMessage = "====== *Bot Template Developed by @DSMan97* ======= \n\n";
        bot.sendTextMessage(ownerMessage + menus.getHelpMenu());
    }
}
