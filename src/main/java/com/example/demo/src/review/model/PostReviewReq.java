package com.example.demo.src.review.model;

import lombok.Getter;

@Getter
public class PostReviewReq {
    private Integer storeIdx;
    private Integer orderIdx;
    private double rating;
}
