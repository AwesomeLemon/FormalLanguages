package com.mtgrammars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Stream;

/**
 * Created by Alex on 10.10.2016.
 */
public class Grammar {
    HashSet<Symbol> nonterminals;
    HashSet<Symbol> terminals;
    Symbol Axiom;
    List<Production> productions;

    public Grammar(HashSet<Symbol> nonterminals, HashSet<Symbol> terminals, Symbol axiom, List<Production> productions) {
        this.nonterminals = nonterminals;
        this.terminals = terminals;
        Axiom = axiom;
        this.productions = productions;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "nonterminals=" + nonterminals +
                ", terminals=" + terminals +
                ", Axiom=" + Axiom +
                ", productions=" + productions +
                '}';
    }

    //emulates starting from (4): A3 -> [eps, blank]A3
    public List<Integer> emulateGrammar0Partially(List<Symbol> input, int maxTapeCount) {
        CompositeSymbol epsBlankSym = new CompositeSymbol("eps", "blank");
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < maxTapeCount; i++) {
            input.add(epsBlankSym);
        }
        while(true) {
            int prodInd = performFirstFromLeftProduction(input);
            if (prodInd == -1) {
                return res;
            }
            res.add(prodInd);
        }
    }

    private int performFirstFromLeftProduction(List<Symbol> input) {
        int productionStartInd = grammar0FindFirst6thTypeProduction();
        for (int i = productionStartInd; i < productions.size(); i++) {
            List<Symbol> left = productions.get(i).left;
            List<Symbol> right = productions.get(i).right;
            int ind = Collections.indexOfSubList(input, left);
            if (ind < 0 || ind + left.size() >= input.size()) continue;
            List<Symbol> inputSubList = input.subList(ind, ind + left.size());
            if (!(inputSubList.equals(left))) {
                int bsmth = 1;
            }
            for (int j = 0; j < left.size(); j++) {
                if (j + ind >= input.size()) {
                    int a = 1;
                }
                input.remove(ind);
            }
            for (int j = 0; j < right.size(); j++) {
                input.add(j + ind, right.get(j));
            }
            return i;
        }
        return -1;
    }

    private int grammar0FindFirst6thTypeProduction() {
        Symbol eps = new Symbol("eps");
        for (int i = 0; i < productions.size(); i++) {
            if (productions.get(i).right.size() > 1) continue;
            if (productions.get(i).right.get(0).equals(eps)) return i + 1;
        }
        return -1;
    }
}
