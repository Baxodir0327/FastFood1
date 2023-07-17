package org.example.server.service;

import lombok.SneakyThrows;
import org.example.server.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.example.client.bot.BotConstants.BACK;
import static org.example.client.bot.BotConstants.LOCATION;
import static org.example.server.enums.State.CREATED;

public class ShareLocationService {
    /**
     * @author Bilolbek
     * @param chatId
     * @description this method shareLocation and back buttons created
     */
    @SneakyThrows
    public static ReplyKeyboardMarkup shareLocation(Long chatId, User user){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow locationRow = new KeyboardRow();
        KeyboardRow backRow = new KeyboardRow();

        KeyboardButton location = new KeyboardButton();
        location.setText(LOCATION);
        location.setRequestLocation(true);

        KeyboardButton back = new KeyboardButton();
        location.setText(BACK);

        locationRow.add(location);
        backRow.add(back);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(locationRow);
        keyboardRows.add(backRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        user.setState(CREATED);
        return replyKeyboardMarkup;
    }
    }
