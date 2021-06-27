package com.example.demo.src.review.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@ToString
@JsonInclude(NON_EMPTY)
public class GetReviewPreviewRes {
    private int reviewIdx;
    private int storeIdx;
    private String storeName;
    private int rating;
    private String writingTimeStamp;
    private String contents;
    private String orderMenus;
    private List<String> reviewImageUrls = new ArrayList<>();
    private int likeCount;
    private int remainingReviewTime;

    public GetReviewPreviewRes(int reviewIdx, int storeIdx, String storeName, int rating, String writingTimeStamp, String contents, String orderMenus, int likeCount, int remainingReviewTime) {
        this.reviewIdx = reviewIdx;
        this.storeIdx = storeIdx;
        this.storeName = storeName;
        this.rating = rating;
        this.writingTimeStamp = writingTimeStamp;
        this.contents = contents;
        this.orderMenus = orderMenus;
        this.likeCount = likeCount;
        this.remainingReviewTime = remainingReviewTime;
    }
}
