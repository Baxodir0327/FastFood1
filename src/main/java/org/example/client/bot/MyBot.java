package org.example.client.bot;

import org.example.server.convertor.UserConverter;
import org.example.server.enums.State;
import org.example.server.model.User;
import org.example.server.service.CategoryService;
import org.example.server.service.CreateButtonService;
import org.example.server.service.ProductService;
import org.example.server.service.UserService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.example.client.bot.BotConstants.*;


public class MyBot extends TelegramLongPollingBot {
    public MyBot(String botToken) {
        super(botToken);
    }

    private static UserService userService = new UserService();
    private static UserConverter userConverter = new UserConverter();
    private static ProductService productService = new ProductService();
    private static CategoryService categoryService = new CategoryService();
    private static CreateButtonService createButtonService = new CreateButtonService();
    private static Pages pages = new Pages();


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String userName = message.getChat().getUserName();

            User user = userConverter.convertUser(chatId, userName);

            if (message.hasText()) {
                String text = message.getText();
                if (user.getState().equals(State.START) && text.equals("/start")) {
                    myExecute(chatId, FIRST_MSG);
                    user.setState(State.ENTER_NAME);
                    userService.update(user);
                } else if (text.equals("/start") && user.getPhoneNumber()!=null) {
                    ReplyKeyboard replyKeyboard = pages.mainPage(createButtonService, isAdmin(user.getPhoneNumber()));
                    myExecute(chatId,"Welcome",replyKeyboard);
                    user.setState(State.CHOOSE_MAIN_PAGE_CATEGORY);
                    userService.update(user);
                } else if (user.getState() == State.ENTER_NAME) {
                    user.setFullName(text);
                    user.setState(State.PHONE_NUMBER);
                    userService.update(user);
                    ReplyKeyboardMarkup shareContactButton = createButtonService.createShareContactButton();
                    myExecute(chatId, "enter phone number",
                            shareContactButton);
                } else if (user.getState() == State.MAIN_PAGE) {
                    ReplyKeyboard replyKeyboard = pages.mainPage(createButtonService, isAdmin(user.getPhoneNumber()));
                    myExecute(chatId, "Welcome " + user.getFullName(), replyKeyboard);
                    user.setState(State.CHOOSE_MAIN_PAGE_CATEGORY);
                    userService.update(user);
                } else if (user.getState() == State.CHOOSE_MAIN_PAGE_CATEGORY && text.equals(BOOK_BUTTON)) {
                    System.out.println("Salom");
                    ReplyKeyboardMarkup replyKeyboardMarkup = createButtonService.categoryPageButtons(isAdmin(user.getPhoneNumber()), user);
                    myExecute(chatId, "Nimadan boshlaymiz " + user.getFullName(), replyKeyboardMarkup);
                }
            } else if (message.hasContact()) {
                user.setState(State.MAIN_PAGE);
                String phoneNumber = message.getContact().getPhoneNumber();
                user.setPhoneNumber(phoneNumber);
                userService.update(user);
                ReplyKeyboard replyKeyboard = pages.mainPage(createButtonService, isAdmin(phoneNumber));
                myExecute(chatId, "Welcome " + user.getFullName(), replyKeyboard);
                user.setState(State.CHOOSE_MAIN_PAGE_CATEGORY);
                userService.update(user);
            }
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
