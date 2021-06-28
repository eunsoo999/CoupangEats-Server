package com.example.demo.src.review.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMenuReview {
    private int orderMenuIdx;
    private String menuName;
    private String menuDetail;
    private String menuLiked;
    private String menuBadReason;
    private String menuComment;
}
