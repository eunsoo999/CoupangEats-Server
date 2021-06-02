package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostMenuReviewReq {
    private Integer orderMenuIdx;
    private String menuLiked;
    private String menuComment;
}
