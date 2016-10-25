package com.formallanguages;

import java.util.HashMap;

/**
 * Created by Alex on 11.10.2016.
 */
public final class CompositeSymbol extends Symbol {
    final String value2;
    private static HashMap<Pair<String, String>, CompositeSymbol> symbols = new HashMap<>();

    private CompositeSymbol(String value, String value2) {
        super(value);
        this.value2 = value2;
    }

    public static CompositeSymbol getCompositeSymbol(String value, String value2) {
        Pair<String, String> key = new Pair<>(value, value2);
        if (symbols.containsKey(key)) return symbols.get(key);
        CompositeSymbol cs = new CompositeSymbol(value, value2);
        symbols.put(key, cs);
        return cs;
    }

    private CompositeSymbol(Symbol symbol) {
        super(symbol.value);
        this.value2 = symbol.value;
    }

    public static CompositeSymbol getRepeatedSymbol(Symbol symbol) {
        Pair<String, String> key = new Pair<>(symbol.value, symbol.value);
        if (symbols.containsKey(key)) return symbols.get(key);
        CompositeSymbol cs = new CompositeSymbol(symbol);
        symbols.put(key, cs);
        return cs;
    }

    public static CompositeSymbol getCompositeSymbol(Symbol symbol1, String value2) {
        Pair<String, String> key = new Pair<>(symbol1.value, value2);
        if (symbols.containsKey(key)) return symbols.get(key);
        CompositeSymbol cs = new CompositeSymbol(symbol1.value, value2);
        symbols.put(key, cs);
        return cs;
    }
    @Override
    public String toString() {
        return "(" +
                super.toString() + ", {" + value2 + "}" +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CompositeSymbol that = (CompositeSymbol) o;

        return value2.equals(that.value2);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + value2.hashCode();
        return result;
    }
}
