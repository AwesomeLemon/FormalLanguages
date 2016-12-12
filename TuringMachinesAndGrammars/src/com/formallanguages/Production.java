package com.formallanguages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alex on 11.10.2016.
 */
class Production {
    final List<Symbol>  left;
    final List<Symbol> right;

    public Production(Symbol left, List<Symbol> right) {
        this.left = Stream.of(left).collect(Collectors.toList());
        this.right = right;
    }

    public Production(List<Symbol> left, List<Symbol> right) {
        this.left = left;
        this.right = right;
    }

    public Production(Symbol left, Symbol right) {
        this.left = Stream.of(left).collect(Collectors.toList());
        this.right = Stream.of(right).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Pro{" +
                left +
                " ----> " + right +
                '}';
    }
}
