package com.example.demo.src.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMenuOptionCategorys {
    private String optionCategoryName;
    private String requiredChoiceFlag;
    private int numberOfChoices;
    private List<GetMenuOptions> options = new ArrayList<>();

    public GetMenuOptionCategorys(String optionCategoryName, String requiredChoiceFlag, int numberOfChoices) {
        this.optionCategoryName = optionCategoryName;
        this.requiredChoiceFlag = requiredChoiceFlag;
        this.numberOfChoices = numberOfChoices;
    }
}
