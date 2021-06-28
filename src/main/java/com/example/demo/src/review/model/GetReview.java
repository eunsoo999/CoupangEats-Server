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
public class GetReview {
    private int reviewIdx;
    private String c;
    private String writerName;
    private int rating;
    private String writingTimeStamp;
    private List<String> imageUrls = new ArrayList<>();
    private String contents;
    private String orderMenus;
    private int likeCount;
    private String isLiked; // 도움이돼요 : Y, 도움안돼요 : N, null
    private String isWriter;
    private String isModifiable;

    public GetReview(int reviewIdx, String writerName, int rating, String writingTimeStamp, String contents, String orderMenus, int likeCount, String isLiked, String isWriter, String isModifiable) {
        this.reviewIdx = reviewIdx;
        this.writerName = writerName;
        this.rating = rating;
        this.writingTimeStamp = writingTimeStamp;
        this.contents = contents;
        this.orderMenus = orderMenus;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.isWriter = isWriter;
        this.isModifiable = isModifiable;
    }
}
