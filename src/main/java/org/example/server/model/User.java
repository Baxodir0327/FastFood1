package org.example.server.model;

import lombok.*;
import org.example.server.enums.State;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseModel{
    private String fullName;
    private String username;
    private String lastProduct;
    private String phoneNumber;
    private Long chatId;
    private State state;
    private String chosenCategory;
}