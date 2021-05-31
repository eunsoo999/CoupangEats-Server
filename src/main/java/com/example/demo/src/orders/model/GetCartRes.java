package com.example.demo.src.orders.model;

import com.example.demo.src.coupon.model.GetCartCoupon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCartRes {
    private String mainAddress;
    private String address;
    private GetCartCoupon coupon;
    private String payType;
    private int deliveryPrice;

    public GetCartRes(String mainAddress, String address, String payType) {
        this.mainAddress = mainAddress;
        this.address = address;
        this.payType = payType;
    }
}