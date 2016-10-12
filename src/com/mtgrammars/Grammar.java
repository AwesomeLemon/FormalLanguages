package com.mtgrammars;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

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
                bw.close();
                return new Pair<>(res, tapeToString(tape));
            }

            bw.write(productions.get(prodInd) + ": " + otherTapeToString(tape) + "\n");
            res.add(prodInd);
        }
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

    private void mergeWithGrammar(Grammar that) {
        terminals.addAll(that.terminals);
        nonterminals.addAll(that.nonterminals);
        productions.addAll(that.productions);
    }

    void mergeWithGrammarAndRename(Grammar otherGrammar, String otherTMname, TuringMachine otherTM) {
        replaceStateNameInRightParts(new Symbol(otherTMname), otherGrammar.Axiom );
        replaceStateNameInLeftParts(new Symbol(otherTMname), otherTM.finalStates.stream().map(x -> new Symbol(otherTMname + x.name)).collect(Collectors.toList()));
        mergeWithGrammar(otherGrammar);
    }

    private void replaceStateNameInRightParts(Symbol target, Symbol replacement) {
        for (Production p : productions) {
            for (int i = 0; i < p.right.size(); i++) {
                if (p.right.get(i).equals(target)) p.right.set(i, replacement);
            }
        }
    }

    private void replaceStateNameInLeftParts(Symbol target, List<Symbol> replacements) {
        ArrayList<Production> additionalProds = new ArrayList<>();
        for (Production p : productions) {
            for (int i = 0; i < p.left.size(); i++) {
                if (p.left.get(i).equals(target)) {
                    p.left.set(i, replacements.get(0));
                    for (int j = 1; j < replacements.size(); j++) {
                        Production newp = new Production(p);
                        newp.left.set(i, replacements.get(j));
                        additionalProds.add(newp);
                    }
                }
            }
        }
        productions.addAll(additionalProds);
    }
}
