package com.example.demo.src.review.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Setter
@Getter
@AllArgsConstructor
@JsonInclude(NON_EMPTY)
public class GetReviewRes {
    private int storeIdx;
    private String storeName;
    private int rating;
    private String badReason;
    private String contents;
    private List<String> imageUrls = new ArrayList<>();
    private List<GetMenuReview> menuReviews = new ArrayList<>();
    private GetDeliveryReviewRes deliveryReview;

    public GetReviewRes(int storeIdx, String storeName, int rating, String badReason, String contents, GetDeliveryReviewRes deliveryReview) {
        this.storeIdx = storeIdx;
        this.storeName = storeName;
        this.rating = rating;
        this.badReason = badReason;
        this.contents = contents;
        this.deliveryReview = deliveryReview;
    }
}
