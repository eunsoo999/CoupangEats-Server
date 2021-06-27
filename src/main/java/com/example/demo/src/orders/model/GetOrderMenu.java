package com.example.demo.src.orders.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetOrderMenu {
    private int count;
    private String menuName;
    private String menuDetail;
    private String menuPrice;
    private String menuLiked;
}
