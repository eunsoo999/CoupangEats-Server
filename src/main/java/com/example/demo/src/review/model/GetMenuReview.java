package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMenuReview {
    private int menuReviewIdx;
    private String menuName;
    private String menuDetail;
    private String menuLiked;
    private String menuComment;
}
