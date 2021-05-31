package com.example.demo.src.coupon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PostUserCouponReq {
    private Integer couponIdx;
    private Integer userIdx;
}
