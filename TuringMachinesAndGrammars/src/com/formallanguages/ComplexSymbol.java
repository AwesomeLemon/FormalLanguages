package com.formallanguages;

import java.util.*;

/**
 * Created by Alex on 09.12.2016.
 */
public class ComplexSymbol extends Symbol {
    public List<String> valueList;
    public ComplexSymbol(Symbol symbol) {
        super(symbol);
        valueList = Arrays.asList(symbol.value);
    }
    private static Map<List<Symbol>, ComplexSymbol> symbols = new HashMap<>();
    public static ComplexSymbol get(List<Symbol> symbolList) {
        if (symbols.containsKey(symbolList)) return symbols.get(symbolList);
        ComplexSymbol newSymbol = new ComplexSymbol(symbolList);
        symbols.put(symbolList, newSymbol);
        return newSymbol;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ComplexSymbol that = (ComplexSymbol) o;

        boolean res = valueList != null ? valueList.equals(that.valueList) : that.valueList == null;

        return res;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (valueList != null ? valueList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ComplexSymbol{" +
                valueList +
                '}';
    }
}
