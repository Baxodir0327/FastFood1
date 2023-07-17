package org.example.server.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateButtonService {
    private UUID id;

    public ReplyKeyboardMarkup createReplyButton(List<String> buttonsTitle, boolean shareContact) {

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
                e.setRequestContact(shareContact);
                row.add(e);
            }
        }

        rows.add(row);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setOneTimeKeyboard(shareContact);
        replyKeyboardMarkup.setSelective(shareContact);
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
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
}