package com.example.demo.src.review.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchMenuReviewReq {
    private Integer orderMenuIdx;
    private String menuLiked;
    private String menuBadReason;
    private String menuComment;
}
