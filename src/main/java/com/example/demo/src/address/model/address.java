package com.example.demo.src.address.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class address {
    private int addressIdx;
    private String address;
    private String roadAddress;
    private String detailAddress;
    private String alias;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private int userIdx;
}
