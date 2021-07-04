package com.example.demo.src.review.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class PostReviewReq {
    private Integer orderIdx;
    private Integer storeIdx;
    private Integer rating;
    private String badReason;
    private String contents;
    private List<String> imageUrls = new ArrayList<>();
    private List<PostMenuReviewReq> menuReviews = new ArrayList<>();
    private PostDeliveryReviewReq deliveryReview;
    private Integer userIdx;
}
