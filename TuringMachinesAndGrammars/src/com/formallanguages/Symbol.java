package com.formallanguages;

import java.util.HashMap;

public class Symbol {
    final String value;

    //copy constructor
    public Symbol(Symbol symbol) {
        value = symbol.value;
    }

    Symbol(String value) {
        this.value = value;
    }

    public static Symbol getSymbol(String value) {
        if (symbols.containsKey(value)) return symbols.get(value);
        Symbol symbol = new Symbol(value);
        symbols.put(value, symbol);
        return symbol;
    }

    private static HashMap<String, Symbol> symbols = new HashMap<>();

    @Override
    public String toString() {
        return "{" +  value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Symbol symbol = (Symbol) o;

        return value.equals(symbol.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
