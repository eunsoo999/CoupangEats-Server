package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetPhotoReview {
    private int reviewIdx;
    private String imageUrl;
    private String contents;
    private double rating;
}
