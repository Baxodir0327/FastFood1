package org.example.client.bot;

import org.example.server.service.CategoryService;
import org.example.server.service.CreateButtonService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

public class Pages {
    private static CategoryService categoryService = new CategoryService();

    public ReplyKeyboard mainPage(CreateButtonService createButtonService, boolean admin) {
        List<String> mainButtons = categoryService.getMainPageCategory(admin);
        return createButtonService.createReplyButton(mainButtons);
    }
}
