package com.example.demo.src.store.model;

import com.example.demo.src.review.model.GetPhotoReview;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetStoreRes {
    private String storeName;
    private List<String> imageUrls = new ArrayList<String>();
    private String rating;
    private String reviewCount;
//    private Integer couponIdx; // 쿠폰지급 중인 가게인 경우 쿠폰 idx
//    private String couponPrice; // 쿠폰 할인 가격
    private String hasCoupon; // 유저가 쿠폰을 소지하는지
    private String deliveryTime;
    private int deliveryPrice;
    private int minOrderPrice;
    private String cheetahDelivery;
    private List<GetPhotoReview> photoReviews = new ArrayList<>();

    public GetStoreRes(String storeName, String rating, String reviewCount, String deliveryTime, int deliveryPrice, int minOrderPrice, String cheetahDelivery) {
        this.storeName = storeName;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.deliveryTime = deliveryTime;
        this.deliveryPrice = deliveryPrice;
        this.minOrderPrice = minOrderPrice;
        this.cheetahDelivery = cheetahDelivery;
    }
}
