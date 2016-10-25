package com.formallanguages;

import java.util.List;
import java.util.HashSet;

public class Grammar {
    private final HashSet<Symbol> nonterminals;
    final HashSet<Symbol> terminals;
    private final Symbol axiom;
    final List<Production> productions;

    public Grammar(HashSet<Symbol> nonterminals, HashSet<Symbol> terminals, Symbol axiom, List<Production> productions) {
        this.nonterminals = nonterminals;
        this.terminals = terminals;
        this.axiom = axiom;
        this.productions = productions;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "nonterminals=" + nonterminals +
                ", terminals=" + terminals +
                ", axiom=" + axiom +
                ", productions=" + productions +
                '}';
    }
}