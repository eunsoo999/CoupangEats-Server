package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetDeliveryReviewRes {
    private String deliveryLiked;
    private String deliveryBadReason;
    private String deliveryComment;
}
