package com.mtgrammars;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;

/**
 * Created by Alex on 10.10.2016.
 */
public class Grammar {
    HashSet<Symbol> nonterminals;
    HashSet<Symbol> terminals;
    Symbol axiom;
    List<Production> productions;

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

    //emulates starting after (5): A3 -> eps.
    public Pair<List<Integer>, String> emulateGrammar0Partially(List<Symbol> tape, int maxTapeCount) throws Exception {
        CompositeSymbol epsBlankSym = new CompositeSymbol("eps", "blank");
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < maxTapeCount; i++) {
            tape.add(epsBlankSym);
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));
        while(true) {
            int prodInd = performFirstFromLeftProduction(tape);

            if (prodInd == -1) {
                tapeRemoveEps(tape);
                bw.close();
                if (tapeContainsNonTerminal(tape)) {
                    throw new Exception("Grammar cannot produce string of terminals from given input");
                    //grammar cannot process given input, 'cause corresponding TM doesn't accept it.
                    //or the grammar is wrong.
                }
                return new Pair<>(res, tapeToString(tape));
            }

            bw.write(productions.get(prodInd) + ": " + otherTapeToString(tape) + "\n");
            res.add(prodInd);
        }
    }

    private void tapeRemoveEps(List<Symbol> tape) {
        Symbol eps = new Symbol("eps");
        while (tape.contains(eps)) tape.remove(eps);
    }

    private boolean tapeContainsNonTerminal(List<Symbol> tape) {
        for (Symbol symbol : tape) {
            if (!terminals.contains(symbol)) return true;
        }
        return false;
    }

    private String tapeToString(List<Symbol> tape) {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : tape) {
            if (!symbol.value.equals("eps")) sb.append(symbol.value);
        }
        return sb.toString();
    }
    private String otherTapeToString(List<Symbol> tape) {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : tape) {
            if (symbol.value.contains("q")) sb.append("(").append(symbol.value).append(")");
            if (symbol instanceof CompositeSymbol) {
                String sym = ((CompositeSymbol) symbol).value2;
                if (sym.equals("blank")) {
                    sb.append("_");
                    continue;
                }
                if (!sym.equals("eps")) sb.append(sym);
            }
        }
        return sb.toString();
    }
    class Pair<T1, T2> {
        T1 fst;
        T2 snd;

        public Pair(T1 fst, T2 snd) {
            this.fst = fst;
            this.snd = snd;
        }
    }

    private int performFirstFromLeftProduction(List<Symbol> input) {
        int productionStartInd = grammar0FindFirst6thTypeProduction();
        for (int i = productionStartInd; i < productions.size(); i++) {
            List<Symbol> left = productions.get(i).left;
            List<Symbol> right = productions.get(i).right;
            int ind = Collections.indexOfSubList(input, left);
            if (ind < 0) continue;
            for (int j = 0; j < left.size(); j++) {
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