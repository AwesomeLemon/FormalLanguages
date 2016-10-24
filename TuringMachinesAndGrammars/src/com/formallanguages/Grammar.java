package com.formallanguages;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;

import static com.formallanguages.CompositeSymbol.getCompositeSymbol;
import static com.formallanguages.GrammarUtilities.otherTapeToString;
import static com.formallanguages.SpecialTuringMachineSymbols.*;

/**
 * Created by Alex on 10.10.2016.
 */
public class Grammar {
    private final HashSet<Symbol> nonterminals;
    private final HashSet<Symbol> terminals;
    private final Symbol axiom;
    private final List<Production> productions;

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
        CompositeSymbol epsBlankSym = getCompositeSymbol(EPSILON, BLANK);
        ArrayList<Integer> usedProductions = new ArrayList<>();
        for (int i = 0; i < maxTapeCount; i++) {
            tape.add(epsBlankSym);
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));
        while (true) {
            int prodInd = performFirstFromStartProduction(tape);

            if (prodInd == -1) {
                GrammarUtilities.tapeRemoveEpsilons(tape);
                bw.close();
                if (GrammarUtilities.tapeContainsNonTerminal(tape, terminals)) {
                    throw new Exception("Grammar cannot produce string of terminals from given input");
                    //grammar cannot process given input, 'cause corresponding TM doesn't accept it.
                    //or the grammar is wrong.
                }
                return new Pair<>(usedProductions, GrammarUtilities.tapeToString(tape));
            }

            bw.write(productions.get(prodInd) + ": " + otherTapeToString(tape) + "\n");
            usedProductions.add(prodInd);
        }
    }

    private int performFirstFromStartProduction(List<Symbol> input) {
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
        Symbol eps = Symbol.getSymbol(EPSILON);
        for (int i = 0; i < productions.size(); i++) {
            if (productions.get(i).right.size() > 1) continue;
            if (productions.get(i).right.get(0).equals(eps)) return i + 1;
        }
        return -1;
    }
}