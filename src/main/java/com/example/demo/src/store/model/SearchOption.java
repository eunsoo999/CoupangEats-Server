package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class SearchOption {
    private String lat;
    private String lon;
    private String sort;
    private String cheetah;
    private Integer minDelivery;
    private Integer minOrderPrice;
    private String coupon;
    private String category;
    private String keyword;
}
