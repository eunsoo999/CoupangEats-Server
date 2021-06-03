package com.example.demo.src.review.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPhotoReview {
    private int reviewIdx;
    private String imageUrl;
    private String contents;
    private double rating;
}
