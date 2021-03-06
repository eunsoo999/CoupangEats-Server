package com.example.demo.src.address.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetAddressesRes {
    private int selectedAddressIdx;
    private GetHomeAddress home;
    private GetCompanyAddress company;
    private List<GetAddressRes> addressList = new ArrayList<>();
}
