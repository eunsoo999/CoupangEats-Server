package com.example.demo.src.store.model;

import com.example.demo.src.event.model.GetEventBannerRes;
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
public class GetMainRes {
    private List<GetEventBannerRes> events = new ArrayList<>();
    private List<GetStoreCategoryRes> storeCategories = new ArrayList<>();
    private List<GetStoreSmallBox> onSaleStores = new ArrayList<>();
    private List<GetNewStoreBox> newStores = new ArrayList<>();
    private List<GetStoreMainBox> recommendStores = new ArrayList<>();
    private int totalCount;
    private int cursor;
    private int numOfRows;
}
