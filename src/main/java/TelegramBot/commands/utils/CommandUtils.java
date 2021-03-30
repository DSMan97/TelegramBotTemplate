package TelegramBot.commands.utils;

import TelegramBot.Controller.TelegramBot;
import TelegramBot.dto.CommandDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandUtils {

    private CommandUtils(){
        //private empty constructor
    }

    public static CommandDto getCommand(String message) {
        TelegramBot bot = new TelegramBot();
        String botName =  bot.getBotUsername();
        Pattern pattern = Pattern.compile("^/(?<command>[^\\s@]+)(?:@"+botName+")?(\\s(?<args>.*))?$");
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            CommandDto commandDto = new CommandDto();
            commandDto.setName(matcher.group("command"));
            String args = matcher.group("args");
            if (args != null) {
                commandDto.setArgs(args.split(" "));
            }

            return commandDto;
        }
        return null;
    }


}
