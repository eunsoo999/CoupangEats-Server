package com.example.demo.src.orders.model;

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
public class GetPastOrder {
    private int orderIdx;
    private int storeIdx;
    private String storeName;
    private String imageUrl;
    private String orderDate;
    private String status;
    private List<GetOrderMenu> orderMenus = new ArrayList<>();
    private String orderPrice;
    private String deliveryPrice;
    private String discountPrice;
    private String totalPrice;
    private String payType;
    private Integer reviewIdx;
    private Integer reviewRating;

    public GetPastOrder(int orderIdx, int storeIdx, String storeName, String imageUrl, String orderDate, String status, String orderPrice, String deliveryPrice, String discountPrice, String totalPrice, String payType, Integer reviewIdx, Integer reviewRating) {
        this.orderIdx = orderIdx;
        this.storeIdx = storeIdx;
        this.storeName = storeName;
        this.imageUrl = imageUrl;
        this.orderDate = orderDate;
        this.status = status;
        this.orderPrice = orderPrice;
        this.deliveryPrice = deliveryPrice;
        this.discountPrice = discountPrice;
        this.totalPrice = totalPrice;
        this.payType = payType;
        this.reviewIdx = reviewIdx;
        this.reviewRating = reviewRating;
    }
}