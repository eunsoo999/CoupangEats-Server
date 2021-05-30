package com.example.demo.src.store.model;

import com.example.demo.src.coupon.model.GetStoreCoupon;
import com.example.demo.src.menu.model.GetMenuByCategory;
import com.example.demo.src.menu.model.GetMenus;
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
    private GetStoreCoupon coupon;
    private String deliveryTime;
    private int deliveryPrice;
    private int minOrderPrice;
    private String cheetahDelivery;
    private List<GetPhotoReview> photoReviews = new ArrayList<>();
    private List<GetMenuByCategory> menuCategories = new ArrayList<>();


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
