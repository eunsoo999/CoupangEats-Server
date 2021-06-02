package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class PostReviewReq {
    private Integer orderIdx;
    private Integer storeIdx;
    private Integer rating;
    private String contents;
    private List<String> images = new ArrayList<>();
    private List<PostMenuReviewReq> menuReviews = new ArrayList<>();
    private String deliveryLiked;
    private String deliveryComment;
    private Integer userIdx;
}
