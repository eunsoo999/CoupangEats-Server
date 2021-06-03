package com.example.demo.src.store.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
public class GetSearchStoresRes {
    private int totalCount;
    private List<GetStoreMainBox> searchStores = new ArrayList<>();
}
