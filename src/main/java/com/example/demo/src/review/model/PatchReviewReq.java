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
public class PatchReviewReq {
    private Integer rating;
    private String badReason;
    private String contents;
    private String modifiedImageFlag;
    private List<String> imageUrls = new ArrayList<>();
    private List<PatchMenuReviewReq> menuReviews = new ArrayList<>();
    private PostDeliveryReviewReq deliveryReview;
}
