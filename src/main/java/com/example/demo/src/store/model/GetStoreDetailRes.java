package com.example.demo.src.store.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetStoreDetailRes {
    private String latitude;
    private String longitude;
    private String storeName;
    private String phone;
    private String address;
    private String ceoName;
    private String businessNumber;
    private String companyName;
    private String businessHours;
    private String introduction;
    private String notice;
    private String countryOfOrigin;
}
