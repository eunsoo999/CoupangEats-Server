package com.example.demo.src.coupon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCartCoupon {
    private String redeemStatus; // 적용상태
    private int couponCount; // 사용 가능 쿠폰개수 ok
    private Integer couponIdx; // 적용 쿠폰 idx
    private int discountPrice; // 할인가격

    public GetCartCoupon(String redeemStatus, Integer couponIdx, int discountPrice) {
        this.redeemStatus = redeemStatus;
        this.couponIdx = couponIdx;
        this.discountPrice = discountPrice;
    }
}
