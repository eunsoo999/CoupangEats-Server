package com.example.demo.src.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetEvents {
    private int eventIdx;
    private String bannerUrl;
    private String endDate;
}
