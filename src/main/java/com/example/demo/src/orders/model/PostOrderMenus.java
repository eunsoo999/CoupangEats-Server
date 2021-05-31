package com.example.demo.src.orders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostOrderMenus {
    private String menuName;
    private String menuDetail;
    private Integer count;
    private Integer totalPrice;
}
