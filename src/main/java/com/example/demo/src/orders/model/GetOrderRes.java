package com.example.demo.src.orders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetOrderRes {
    private int storeIdx;
    private String storeName;
    private List<GetOrderMenuRes> orderMenus = new ArrayList<>();

    public GetOrderRes(int storeIdx, String storeName) {
        this.storeIdx = storeIdx;
        this.storeName = storeName;
    }
}
