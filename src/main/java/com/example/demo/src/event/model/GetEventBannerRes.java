package com.example.demo.src.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetEventBannerRes {
    private int eventIdx;
    private String bannerUrl;
}
