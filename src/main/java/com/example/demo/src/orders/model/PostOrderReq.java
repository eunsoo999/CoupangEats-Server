package com.example.demo.src.orders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class PostOrderReq {
    private String address;
    private Integer storeIdx;
    private List<PostOrderMenus> orderMenus = new ArrayList<>();
    private Integer couponIdx;
    private Integer orderPrice;
    private Integer deliveryPrice;
    private Integer discountPrice;
    private Integer totalPrice;
    private String storeRequests;
    private String CheckEchoProduct;
    private String deliveryRequests;
    private String payType;
    private Integer userIdx;
}
