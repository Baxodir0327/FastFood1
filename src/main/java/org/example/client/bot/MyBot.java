package org.example.client.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import static org.example.client.bot.BotConstants.*;


public class MyBot extends TelegramLongPollingBot {
//    private static UserService userService = new UserService();
//    private static UserConverter userConverter = new UserConverter();
//    private static ProductService productService = new ProductService();
//    private static CategoryService categoryService = new CategoryService();
//    private static CreateButtonService createButtonService = new CreateButtonService();


    public MyBot(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {


        } else if (update.hasCallbackQuery()) {

        }

    }

    private static boolean isAdmin(String phoneNumber) {
        return ADMIN_NUMBERS.contains(phoneNumber);
    }

    private Message myExecute(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), text);
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void myExecute(Long chatId, String message, ReplyKeyboard r) {
        SendMessage s = new SendMessage();
        s.setChatId(chatId);
        s.setText(message);
        s.setReplyMarkup(r);
        try {
            execute(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }
}

