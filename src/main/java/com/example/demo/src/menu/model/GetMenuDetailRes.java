package com.example.demo.src.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMenuDetailRes {
    private String menuName;
    private List<String> imageUrls = new ArrayList<>();
    private String introduction;
    private int price;
    private List<GetMenuOptionCategorys> menuOptions = new ArrayList<>();

    public GetMenuDetailRes(String menuName, String introduction, int price) {
        this.menuName = menuName;
        this.introduction = introduction;
        this.price = price;
    }
}
