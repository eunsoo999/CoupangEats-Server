package com.example.demo.src.bookmark.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PatchBookmarksStatusReq {
    private List<Integer> storeIdxList = new ArrayList<>();
}
