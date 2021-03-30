package TelegramBot.commands;

import TelegramBot.Controller.TelegramBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandExecutor {
    void execute(TelegramBot bot, int replyMessageId, Update update, String... args);
}
