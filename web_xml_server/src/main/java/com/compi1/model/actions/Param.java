package com.compi1.model.actions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@Builder @ToString
public class Param {
    private ParamType type;
    private String value;
}
