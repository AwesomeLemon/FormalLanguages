package com.mtgrammars;

/**
 * Created by Alex on 11.10.2016.
 */
public class CompositeSymbol extends Symbol {
    final String value2;

    public CompositeSymbol(CompositeSymbol symbol) {
        super(symbol.value);
        value2 = symbol.value2;
    }

    public CompositeSymbol(String value, String value2) {
        super(value);
        this.value2 = value2;
    }


    public CompositeSymbol(Symbol symbol) {
        super(symbol.value);
        this.value2 = symbol.value;
    }
    public CompositeSymbol(Symbol symbol1, Symbol symbol2) {
        super(symbol1.value);
        this.value2 = symbol2.value;
    }
    public CompositeSymbol(Symbol symbol1, String value2) {
        super(symbol1.value);
        this.value2 = value2;
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
