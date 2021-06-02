package com.example.demo.src.address.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PatchAddressReq {
    private String address; // 앱에서 지도 이동 시, 변경됨
    private String roadAddress; // 앱에서 지도 이동 시, 변경됨
    private String detailAddress;
    private String aliasType;
    private String alias;
}
