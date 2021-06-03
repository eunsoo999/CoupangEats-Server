package com.example.demo.src.address.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PostAddressReq {
    private String address;
    private String roadAddress;
    private String detailAddress;
    private String aliasType;
    private String alias;
    private Integer userIdx;
}
