package com.example.demo.src.store.model;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMainRes {
    private List<GetStoreSmallBox> onSaleStores = new ArrayList<>();
    // 이벤트 추가
    private List<GetStoreMainBox> recommendStores = new ArrayList<>();
    private int totalCount;
    private int cursor;
    private int numOfRows;
}
