package TelegramBot.Controller;

import TelegramBot.commands.CommandExecutor;
import TelegramBot.commands.annotation.Command;
import TelegramBot.commands.utils.CommandUtils;
import TelegramBot.dto.CommandDto;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.reflections.Reflections;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TelegramBot extends TelegramLongPollingBot  {
    private final Map<String, CommandExecutor> commandExecutorMap = new HashMap<>();

    public TelegramBot() {
        Reflections reflections = new Reflections("TelegramBot.commands");

        Set<Class<?>> commandClasses = reflections.getTypesAnnotatedWith(Command.class);

        for (Class clazz : commandClasses) {
            if (CommandExecutor.class.isAssignableFrom(clazz)) {
                createNewInstance(clazz);
            } else {
                System.err.println("Could not create instance of class <" + clazz.getCanonicalName() + ">");
            }

        }
    }

    private void createNewInstance(Class clazz) {
        try {
            CommandExecutor commandExecutor = (CommandExecutor) clazz.getConstructor().newInstance();
            Command command = (Command) clazz.getAnnotation(Command.class);
            System.out.println("Binding Command <" + command.value() + "> to Executor <" + clazz.getCanonicalName() + ">");
            commandExecutorMap.put(command.value(), commandExecutor);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public static String BOT_TOKEN = " "; //GodFather Created Bot Token
    public static String BOT_NAME = " "; //GodFather Created Bot username

    private boolean isCommand = false;


    GetFile getFile = new GetFile();

    SendMessage message = new SendMessage();
    SendDocument sendDocument = new SendDocument();

    public void onUpdateReceived(Update update) {
        String document_id;
        String response = "";
        String command = "";
        String text = update.getMessage().getText();


        message.setChatId(update.getMessage().getChatId());
        sendDocument.setChatId(update.getMessage().getChatId());

        if(update.getMessage().getText().startsWith("/")){
            isCommand = true;
        }



        if(isCommand){
            command = text;
        }



        if(update.getMessage().getText().contains("/")){
            Long chatId = update.getMessage().getChatId();
            int userId = update.getMessage().getFrom().getId();
            System.out.println(isAdmin(chatId, userId));
            if(isAdmin(chatId, userId)){
                command = "/" + update.getMessage().getText().split("/")[1];
                System.out.println(command);
            }

        }


        CommandDto commandDto = CommandUtils.getCommand(command);
        assert commandDto != null;
        if (commandExecutorMap.containsKey(commandDto.getName())) {
            Message message = update.getMessage();
            int replyMessageId = message.getReplyToMessage() != null ? message.getReplyToMessage().getMessageId() : 0;
            try {

                if (message.getChat().isSuperGroupChat()){
                    if(update.getMessage().getText().startsWith("/")) {
                        this.execute(new DeleteMessage(message.getChatId(), message.getMessageId()));

                    }
                    if(update.getMessage().getText().contains("/") && !update.getMessage().getText().startsWith("/")) {

                        this.execute(new DeleteMessage(message.getChatId(), message.getMessageId()));
                        this.sendTextMessage(message.getFrom().getFirstName() + " dice: " + message.getText().replace("/", ""), replyMessageId);

                        }
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }


            commandExecutorMap.get(commandDto.getName()).execute(this, replyMessageId, update, commandDto.getArgs());
        }

        if(isCommand){
            setLog(command, update);
        }
    }

    public boolean isAdmin(Long chatId, int userId){
        boolean isAdmin = false;
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.telegram.org/bot"+ BOT_TOKEN +"/getChatMember" +
                    "?chat_id="+chatId+"&user_id="+userId);
            request.addHeader("content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(request);
            String json = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

            JSONParser parser = new JSONParser();
            Object object =  parser.parse(json);
            JSONObject jsonObject = (JSONObject) object;
            JSONObject result = (JSONObject) jsonObject.get("result");
            String status = (String) result.get("status");
            System.out.println(status);
            if (status.equals("administrator") || status.equals("creator")){
                isAdmin = true;
            }


        } catch (Exception ex) {
            System.err.println(ex);
        }

        return isAdmin;
    }


    private static String readLineByLineJava(String filePath) throws IOException {
            File file = new File(filePath);
            String result = "";
            if(file.exists()) {
                BufferedReader lector = new BufferedReader(new FileReader(filePath));
                StringBuilder cadena = new StringBuilder();
                String line = null;

                while ((line = lector.readLine()) != null) {
                    cadena.append(line);

                }
                lector.close();
                result = cadena.toString();
            }
        return result;

    }

    public void sendPlainTextMessage(String text)
    {
        this.message.setText(text).enableMarkdown(false);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(String text) {
        sendTextMessage(text, 0);
    }

    public void sendPrivateMessageTo(String userdId, String text){
                message.setChatId(userdId).setText(text).enableMarkdown(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendTextMessage(String text, int replyTo)
    {


        if (replyTo >= 0) {
            message.setReplyToMessageId(replyTo).setText(text).enableMarkdown(true);
        } else {
            message.setText(text).enableMarkdown(true);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDocumentMessageWithText(String file_id, String text) {
        String document = getFile.setFileId(file_id).getFileId();
        this.sendDocument.setDocument(document);

        try {
            this.sendTextMessage(text);
            execute(sendDocument);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocument(String file_id) {
        String document = getFile.setFileId(file_id).getFileId();
        this.sendDocument.setDocument(document);

        try {

            execute(sendDocument);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void setLog(String comando, Update update)
    {
        System.out.println("Comando "+ comando + " ejecutado por: " + update.getMessage().getFrom().getFirstName());
    }

    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }


}
