package com.formallanguages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alex on 09.12.2016.
 */
public class ComplexSymbol extends Symbol {
    public List<String> valueList;
    public ComplexSymbol(Symbol symbol) {
        super(symbol);
        valueList = Arrays.asList(symbol.value);
    }

    public ComplexSymbol(String value) {
        super(value);
        valueList = Arrays.asList(value);
    }

//    public ComplexSymbol(List<String> valueList){
//        super(valueList.get(0));
//        this.valueList = valueList;
//    }

    public ComplexSymbol(List<Symbol> symbolList) {
        super(symbolList.get(0));
        this.valueList = new ArrayList<>();
        for (Symbol symbol : symbolList) {
            this.valueList.add(symbol.value);
        }
    }

    @Override
    public String toString() {
        return "ComplexSymbol{" +
                valueList +
                '}';
    }
}
