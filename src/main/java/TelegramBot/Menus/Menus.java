package TelegramBot.Menus;

import java.util.ArrayList;
import java.util.List;

public class Menus {

    public String buildMenu(String[] commands)
    {
        List<String> commandsMenu = new ArrayList<String>();

        for (String command : commands) {
            commandsMenu.add(command + "\n");
        }

        String formmatedMenu = commandsMenu.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(",", "");

        return String.format("Available commands: \n" + " %s",
                formmatedMenu
        );
    }

    public String getHelpMenu() {
        String[] commands  = {
                "/tell - send messages by a bot if user is admin",
                "/help - Show this menu",

        };
        return this.buildMenu(commands);
    }
}
