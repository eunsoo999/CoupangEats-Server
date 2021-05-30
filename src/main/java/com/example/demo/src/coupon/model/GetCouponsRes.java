package com.example.demo.src.coupon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetCouponsRes {
    private int userCouponIdx;
    private String couponName;
    private String discountPrice;
    private String minOrderPrice;
    private String expirationDate;
    private String status; // 사용가능, 사용완료, 기간만료 상태
}
