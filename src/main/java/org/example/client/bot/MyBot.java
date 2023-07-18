package org.example.client.bot;

import lombok.SneakyThrows;
import org.example.server.convertor.UserConverter;
import org.example.server.enums.State;
import org.example.server.model.Category;
import org.example.server.model.Product;
import org.example.server.model.User;
import org.example.server.service.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.example.client.bot.BotConstants.*;


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


    @SneakyThrows
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

                } else if (text.equals("◀️ Qaytish")) {
                    ReplyKeyboard replyKeyboard = pages.back(user,admin,createButtonService);
                    myExecute(chatId, "Tanlang", replyKeyboard);
                    userService.update(user);

                } else if (text.equals("/start") && user.getPhoneNumber() != null) {
                    ReplyKeyboard replyKeyboard = pages.mainPage(createButtonService, admin);
                    myExecute(chatId, "Welcome", replyKeyboard);

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
                } else if (user.getState().equals(CREATED) && text.equals(BACK)) {
                    //todo
                } // agar state created bo'lsa user buyurtma qilmoqchi va telefon raqami
                // yuborilsa unga qo'ngi'roq bo'ladi!
                else if (user.getState().equals(CREATED)) {
                    createButtonService.createShareContactButton(BACK);
                    user.setState(SUCCESSFULLY);
                }else if (text.equals(BACK)){
                    user.setState(/*nimadir*/CREATED);
                    user.setState(State.PRESS_CATEGORY_BUTTON);
                    userService.update(user);
                } else if (admin && user.getState().equals(State.PRESS_CATEGORY_BUTTON)) {
                    if (text.equals("+ Add Category")) {
                        myExecute(chatId, "Enter categroy name");
                        user.setState(State.ADD_CATEGORY);
                    } else if (text.equals("- Delete Category")) {
                        myExecute(chatId, "Enter categroy name");
                        user.setState(State.DELETE_CATEGORY);
                    } else {
                        productPage(chatId, user, admin, text);
                        user.setChosenCategory(text);
                    }
                    userService.update(user);
                }else if (admin && user.getState().equals(State.ADD_CATEGORY)) {
                    Category category = new Category(text, null);
                    categoryService.add(category);
                    user.setState(State.PRESS_CATEGORY_BUTTON);
                    userService.update(user);

                    ReplyKeyboardMarkup keyboardMarkup = createButtonService.categoryPageButtons(admin, user);
                    myExecute(chatId, text + " category added", keyboardMarkup);

                } else if (admin && user.getState().equals(State.DELETE_CATEGORY)) {
                    categoryService.deleteByName(text);
                    user.setState(State.PRESS_CATEGORY_BUTTON);
                    userService.update(user);

                    ReplyKeyboardMarkup keyboardMarkup = createButtonService.categoryPageButtons(admin, user);
                    myExecute(chatId, text + " category deleted", keyboardMarkup);
                } else if (user.getState() == State.PRESS_CATEGORY_BUTTON) {
                    productPage(chatId, user, admin, text);
                } else if (admin && text.equals("+ Add Product") && user.getState().equals(State.CHOOSE_PRODUCT)) {
                    myExecute(chatId, "Enter product name: ");
                    user.setState(State.ENTER_PRODUCT_NAME);
                    userService.update(user);
                } else if (admin && text.equals("- Delete Product")) {
                    myExecute(chatId, "Enter deleted product name");
                    user.setState(State.DELETE_PRODUCT);
                    userService.update(user);
                } else if (admin && user.getState().equals(State.DELETE_PRODUCT)) {
                    productService.deleteByName(text);
                    user.setState(State.PRESS_CATEGORY_BUTTON);
                    userService.update(user);
                    ReplyKeyboardMarkup replyKeyboardMarkup = createButtonService.categoryPageButtons(admin, user);
                    myExecute(chatId, "category deleted successfully", replyKeyboardMarkup);
                }else if (user.getState().equals(State.CHOOSE_PRODUCT)) {
                    String f=text;
                    Product product1 = productService.getAll().stream().filter(product -> product.getName().equals(f)).findFirst().get();
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setPhoto(new InputFile(new java.io.File(product1.getPhotoUrl())));
                    sendPhoto.setChatId(chatId);
                    sendPhoto.setCaption(product1.getName()+"\n" +
                            "Narxi:"+product1.getPrice());
                    execute(sendPhoto);
                    InlineKeyboardMarkup inlineKeyboard = createButtonService.createInlineKeyboard(List.of("1", "2", "3","4","5","6"), 3);
                    myExecute(chatId,"nechta kiritishni tanlang",inlineKeyboard);

                }
                else if (admin && user.getState().equals(State.ENTER_PRODUCT_NAME)) {
                    Product product = Product.builder()
                            .name(text)
                            .categoryName(user.getChosenCategory())
                            .build();
                    productService.add(product);
                    user.setLastProduct(text);
                    myExecute(chatId, "Send product photo");
                    user.setState(State.ENTER_PRODUCT_URL);
                    userService.update(user);
                } else if (admin && user.getState() == State.ENTER_PRODUCT_PRICE) {
                    Product product1 = productService.getAll().stream().filter(product -> Objects.equals(product.getName(), user.getLastProduct())).findFirst().get();
                    product1.setPrice(Double.parseDouble(text));
                    productService.update(product1);
                    user.setState(State.PRESS_CATEGORY_BUTTON);
                    userService.update(user);
                    ReplyKeyboardMarkup replyKeyboardMarkup = createButtonService.categoryPageButtons(admin, user);
                    myExecute(chatId, "pruduct succesfully added" + product1.getName(), replyKeyboardMarkup);

                }
                //Share location
            } else if (message.hasLocation()) {
                ShareLocationService.shareLocation(chatId, user);
                user.setLocation(message.getLocation());
                user.setState(CREATED);
            } else if (message.hasContact()) {
                user.setState(State.MAIN_PAGE);
                String phoneNumber = message.getContact().getPhoneNumber();
                user.setPhoneNumber(phoneNumber);
                userService.update(user);
                ReplyKeyboard replyKeyboard = pages.mainPage(createButtonService, isAdmin(phoneNumber));
                myExecute(chatId, "Welcome " + user.getFullName(), replyKeyboard);
                user.setState(State.CHOOSE_MAIN_PAGE_CATEGORY);
                userService.update(user);

            } else if (message.hasPhoto() && admin && user.getState().equals(State.ENTER_PRODUCT_URL)) {
                System.out.println("rasm");
                PhotoSize photo = message.getPhoto().stream().sorted((o1, o2) -> o2.getWidth() * o2.getHeight() - o1.getWidth() * o1.getHeight())
                        .findFirst()
                        .orElse(null);
                if (photo != null) {
                    System.out.println("Rasm");
                    GetFile getFile = new GetFile();
                    getFile.setFileId(photo.getFileId());
                    try {
                        File file = execute(getFile);
                        String filePath = (file).getFilePath();
                        System.out.println(filePath);
                        System.out.println(user.getLastProduct());
                        String fileUrl = "https://api.telegram.org/file/bot" + BotConstants.TOKEN + "/" + filePath;
                        String savePath = "src/main/resources/" + filePath;
                        saveImageFromUrl(fileUrl, savePath);
                        Optional<Product> optionalProduct = productService.getAll()
                                .stream()
                                .filter(product -> Objects.equals(product.getName(), user.getLastProduct())).findFirst();
                        Product product = optionalProduct.get();
                        product.setPhotoUrl(savePath);
                        productService.update(product);
                        user.setState(State.ENTER_PRODUCT_PRICE);
                        userService.update(user);
                        myExecute(chatId, "Enter Product price");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else if (user.getState().equals(CREATED) && message.hasContact()) {
                String phoneNumber = message.getContact().getPhoneNumber();
                user.setPhoneNumberOfTheRecipient(phoneNumber);
                myExecute(chatId, "Siz bilan tez orada bog'lanamiz!");

            }
        } else if (update.hasCallbackQuery()) {

        }

    }

    private void productPage(Long chatId, User user, boolean admin, String text) {
        ReplyKeyboardMarkup replyKeyboardMarkup = createButtonService.productPageButtons(text, admin);
        myExecute(chatId, "tanlang :", replyKeyboardMarkup);
        user.setState(State.CHOOSE_PRODUCT);
        userService.update(user);
    }

    private void saveImageFromUrl(String fileUrl, String savePath) throws IOException {
        URL url = new URL(fileUrl);
        try (InputStream inputStream = url.openStream()) {
            Path outputPath = Path.of(savePath);
            Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
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
