package com.example.demo.src.coupon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PatchUserCouponRes {
    private Integer couponIdx;
    private int discountPrice;
}
