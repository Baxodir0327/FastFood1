package org.example.server.service;

import org.example.server.model.Basket;
import org.example.server.model.Category;
import org.example.server.model.Product;
import org.example.server.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.stream.Collectors;

public class CreateButtonService {
    private static BasketService basketService = new BasketService();

    private static ProductService productService = new ProductService();


    public ReplyKeyboardMarkup createReplyButton(List<String> buttonsTitle) {

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (int i = 0; i < buttonsTitle.size(); i++) {
            if (i == 0) {
                row.add(new KeyboardButton(buttonsTitle.get(i)));
                rows.add(row);
                row = new KeyboardRow();
            } else {
                if (i != 1 && i % 2 != 0) {
                    rows.add(row);
                    row = new KeyboardRow();
                }
                KeyboardButton e = new KeyboardButton(buttonsTitle.get(i));
                row.add(e);
            }
        }

        rows.add(row);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup createShareContactButton() {

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton shareContact = new KeyboardButton("Share Contact");
        shareContact.setRequestContact(true);
        row.add(shareContact);
        rows.add(row);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup createShareContactButton(String buttonMessage) {

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton shareContact = new KeyboardButton("Share Contact");
        KeyboardButton button = new KeyboardButton(buttonMessage);
        shareContact.setRequestContact(true);
        row.add(shareContact);
        row1.add(button);
        rows.add(row);
        rows.add(row1);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public InlineKeyboardMarkup createInlineKeyboard(List<String> keyboasrdList, int numberOfRows) {

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int maxNumber = keyboasrdList.size();
        int currentNumber = 1;

        while (currentNumber <= maxNumber) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int i = 0; i < numberOfRows && currentNumber <= maxNumber; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(keyboasrdList.get(i));
                String id = keyboasrdList.get(i);
                button.setCallbackData(id);
                row.add(button);
                currentNumber++;
            }
            rows.add(row);
        }
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private List<String> orderButton() {
        return List.of("\uD83D\uDE97 Buyurtma qilish");
    }

    private List<String> menuButton() {
        return List.of("\uD83C\uDF7D Menyu");
    }

    private List<String> basketButton(int count) {
        return List.of("\uD83D\uDED2 Savat (%d)".formatted(count));
    }

    private List<String> backButton() {
        return List.of("◀\uFE0F Qaytish");
    }

    public ReplyKeyboardMarkup categoryPageButtons(boolean admin, User user) {
        List<String> userCategoryButton = new ArrayList<>();
        List<Basket> all = basketService.getAll();
        Optional<Basket> first = all.stream().filter(basket -> basket.getUser().getChatId().equals(user.getChatId())).findFirst();

        if (first.isEmpty()) {
            userCategoryButton.addAll(menuButton());
        } else {
            userCategoryButton.addAll(orderButton());
            userCategoryButton.addAll(menuButton());
            int size = first.get().getBasketProducts().size();
            userCategoryButton.addAll(basketButton(size));
        }

        CategoryService categoryService = new CategoryService();
        userCategoryButton.addAll(categoryService.getAll().stream().filter(category -> category.getParentName() == null).map(Category::getName).toList());
        if (admin) {
            userCategoryButton.addAll(List.of("+ Add Category", "- Delete Category"));
        }
        userCategoryButton.addAll(backButton());
        return createReplyButton(userCategoryButton);
    }

    public ReplyKeyboardMarkup productPageButtons(String text, boolean admin) {
        System.out.println(text);
        List<String> productList = productService.getAll().stream().filter(product -> product.getCategoryName().equals(text)).map(product -> product.getName()).collect(Collectors.toList());
        if (admin) {
            productList.addAll(List.of("+ Add Product", "- Delete Product"));
        }
        productList.addAll(backButton());
        return createReplyButton(productList);
    }
}