package com.example.demo.src.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMenus {
    private String bestOrderMenu;
    private String bestReview;
    private int menuIdx;
    private String menuName;
    private String imageUrl;
    private int price;
    private String introduction;
}
