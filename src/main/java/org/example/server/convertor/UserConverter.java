package org.example.server.convertor;


import org.example.server.enums.State;
import org.example.server.model.User;
import org.example.server.service.UserService;

import java.util.Optional;

import static org.example.server.enums.State.START;

public class UserConverter {
    private  static UserService userService=new UserService();
    public User convertToEntity(Long chatId, String username) {
        Optional<User> optionalUser = userService.getByChatId(chatId);

        User user = optionalUser.orElse(User.builder()
                .chatId(chatId)
                .username(username)
                .state(START)
                .build());


        if (optionalUser.isEmpty()) {
            userService.add(user);
        }
        return user;
    }
}