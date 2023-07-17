package org.example.server.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends BaseModel{
    private String name;
    private String photoUrl;
    private double price;
    private String categoryName;

}
