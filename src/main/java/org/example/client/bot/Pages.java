package org.example.client.bot;

import org.example.server.enums.State;
import org.example.server.model.User;
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

    public ReplyKeyboard back(User user, boolean admin, CreateButtonService createButtonService) {
        if (user.getState() == State.PRESS_CATEGORY_BUTTON) {
            user.setState(State.CHOOSE_MAIN_PAGE_CATEGORY);
            return mainPage(createButtonService, admin);
        } else if (user.getState() == State.CHOOSE_PRODUCT) {
            user.setState(State.PRESS_CATEGORY_BUTTON);
            return createButtonService.categoryPageButtons(admin, user);
        }
        return null;
    }
}
