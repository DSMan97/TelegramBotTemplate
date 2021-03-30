package TelegramBot.commands.impl;

import TelegramBot.Controller.TelegramBot;
import TelegramBot.commands.CommandExecutor;
import TelegramBot.commands.annotation.Command;
import org.telegram.telegrambots.meta.api.objects.Update;

@Command("tell")
public class TellCommandExecutor implements CommandExecutor {
    @Override
    public void execute(TelegramBot bot, int replyMessageId, Update update, String... args) {
        String response = update.getMessage().getText().replace("/tell", "");
        Long chatId = update.getMessage().getChatId();
        int userId = update.getMessage().getFrom().getId();
        if(bot.isAdmin(chatId, userId)){
            bot.sendTextMessage(response, replyMessageId);
        }

    }
}
