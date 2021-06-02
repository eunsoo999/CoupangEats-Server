package com.example.demo.src.address.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAddressDetailRes {
    private String address;
    private String roadAddress;
    private String detailAddress;
    private String aliasType;
    private String alias;
    private String latitude;
    private String longitude;
}
