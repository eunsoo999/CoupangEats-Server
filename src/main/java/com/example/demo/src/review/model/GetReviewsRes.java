package com.example.demo.src.review.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(NON_EMPTY)
public class GetReviewsRes {
    private String title;
    private double totalRating;
    private String reviewCount;
    private List<GetReview> reviews = new ArrayList<>();

    public GetReviewsRes(String title, double totalRating, String reviewCount) {
        this.title = title;
        this.totalRating = totalRating;
        this.reviewCount = reviewCount;
    }
}
