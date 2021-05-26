package com.example.demo.src.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetDuplicatedPhoneRes {
    private String isDuplicated;
    private String duplicatedEmail;
}
