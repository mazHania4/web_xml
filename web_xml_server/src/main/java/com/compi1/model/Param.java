package com.compi1.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter @Builder @ToString
public class Param {
    private ParamType type;
    private String value;
}
