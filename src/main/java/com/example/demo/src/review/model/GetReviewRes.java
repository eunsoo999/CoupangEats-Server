package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class GetReviewRes {
    private int storeIdx;
    private String storeName;
    private double rating;
    private String contents;
    private List<String> images = new ArrayList<>();
    private List<GetMenuReview> menuReviews = new ArrayList<>();
    private String deliveryLiked;
    private String deliveryComment;

    public GetReviewRes(int storeIdx, String storeName, double rating, String contents, String deliveryLiked, String deliveryComment) {
        this.storeIdx = storeIdx;
        this.storeName = storeName;
        this.rating = rating;
        this.contents = contents;
        this.deliveryLiked = deliveryLiked;
        this.deliveryComment = deliveryComment;
    }
}