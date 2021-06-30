package com.example.demo.src.bookmark.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetBookmarkRes {
    private int storeIdx;
    private String storeName;
    private String imageUrl;
    private String cheetahDelivery;
    private String totalReview;
    private String distance;
    private String deliveryTime;
    private String deliveryPrice;
    private String coupon;
}
