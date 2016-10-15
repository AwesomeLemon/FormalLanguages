package com.mtgrammars;

/**
 * Created by Alex on 11.10.2016.
 */
public class Symbol {
    final String value;

    public Symbol(Symbol symbol) {
        value = symbol.value;
    }

    public Symbol(String value) {
        this.value = value;
    }

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
