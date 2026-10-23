package com.base.infrastructure.utils.query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryUtilsDto {

    private int indice;
    private String field;
    private String condition;
    private Boolean bracketOpen;
    private Boolean doubleBracketOpen;
    private Boolean bracketClose;
    private Boolean doubleBracketClose;
    private Boolean like;
    private Boolean upper;
    private Boolean or;
    private Object value;

}
