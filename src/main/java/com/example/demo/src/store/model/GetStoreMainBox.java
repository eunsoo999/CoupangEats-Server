package com.example.demo.src.store.model;

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
public class GetStoreMainBox {
    private int storeIdx;
    private List<String> imageUrls = new ArrayList<>();
    private String storeName;
    private String markIcon;
    private String totalReview;
    private String distance;
    private String deliveryPrice;
    private String deliveryTime;
    private String coupon;

    public GetStoreMainBox(int storeIdx, String storeName, String markIcon, String totalReview, String distance, String deliveryPrice, String deliveryTime, String coupon) {
        this.storeIdx = storeIdx;
        this.storeName = storeName;
        this.markIcon = markIcon;
        this.totalReview = totalReview;
        this.distance = distance;
        this.deliveryPrice = deliveryPrice;
        this.deliveryTime = deliveryTime;
        this.coupon = coupon;
    }
}
