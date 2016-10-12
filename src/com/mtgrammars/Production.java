package com.mtgrammars;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alex on 11.10.2016.
 */
class Production {
    List<Symbol>  left;
    List<Symbol> right;

    public Production(Production production) {
        this.left = new ArrayList<>();
        for (Symbol sym : production.left) {
            left.add(new Symbol(sym));
        }
        this.right = new ArrayList<>();
        for (Symbol sym : production.right) {
            right.add(new Symbol(sym));
        }
    }

    public Production(Symbol left, List<Symbol> right) {
        this.left = Stream.of(left).collect(Collectors.toList());
        this.right = right;
    }

    public Production(List<Symbol> left, List<Symbol> right) {
        this.left = left;
        this.right = right;
    }

    public Production(Symbol left, Symbol right1, Symbol right2) {
        this.left = Stream.of(left).collect(Collectors.toList());
        this.right = Stream.of(right1, right2).collect(Collectors.toList());
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
