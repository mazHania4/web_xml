package com.compi1.model.actions;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter @Builder @ToString
public class Action {
    private ActionType type;
    private List<Param> params;
    private List<Attr> attributes;
    private List<String> tags;
}
