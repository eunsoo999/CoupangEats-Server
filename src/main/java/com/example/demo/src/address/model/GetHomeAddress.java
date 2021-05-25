package com.example.demo.src.address.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeAddress {
    private int addressIdx;
    private String mainAddress;
    private String subAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
