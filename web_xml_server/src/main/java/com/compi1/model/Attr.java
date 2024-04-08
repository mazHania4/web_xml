package com.compi1.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter @Builder @ToString
public class Attr {
    private AttrType type;
    private String value;
}
