package com.example.demo.src.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMenuByCategory {
    private String menuCategoryName;
    private String introduction;
    private List<GetMenus> menuList = new ArrayList<>();

    public GetMenuByCategory(String menuCategoryName, String introduction) {
        this.menuCategoryName = menuCategoryName;
        this.introduction = introduction;
    }
}
