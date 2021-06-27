package com.example.demo.src.orders.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetOrderMenuRes {
    private int orderMenuIdx;
    private String orderMenuName;
    private String orderMenuDetail;
}
