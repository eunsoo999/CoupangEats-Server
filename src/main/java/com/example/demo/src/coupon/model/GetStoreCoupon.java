package com.example.demo.src.coupon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStoreCoupon {
    private int couponIdx;
    private int discountPrice;
    private String hasCoupon;

    public GetStoreCoupon(int couponIdx, int discountPrice) {
        this.couponIdx = couponIdx;
        this.discountPrice = discountPrice;
    }
}
