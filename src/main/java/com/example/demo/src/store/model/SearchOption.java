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

    public SearchOption(String lat, String lon, String sort, String cheetah, Integer minDelivery, Integer minOrderPrice) {
        this.lat = lat;
        this.lon = lon;
        this.sort = sort;
        this.cheetah = cheetah;
        this.minDelivery = minDelivery;
        this.minOrderPrice = minOrderPrice;
    }
}
