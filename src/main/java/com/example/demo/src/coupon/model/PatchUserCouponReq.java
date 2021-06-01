package com.example.demo.src.coupon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PatchUserCouponReq {
    private Integer couponIdx;
    private Integer storeIdx;
    private String redeemStatus; // Y : 적용, N : 적용안함
}
