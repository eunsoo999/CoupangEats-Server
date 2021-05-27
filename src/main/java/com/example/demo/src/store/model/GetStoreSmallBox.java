package com.example.demo.src.store.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetStoreSmallBox {
    private int storeIdx;
    private String imageUrl;
    private String storeName;
    private String totalReview;
    private String distance;
    private String coupon;
}
