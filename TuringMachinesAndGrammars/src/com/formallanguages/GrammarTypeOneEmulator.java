package com.formallanguages;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Created by Alex on 11.12.2016.
 */
public class GrammarTypeOneEmulator {
    private final Grammar grammar;
    private String tapeToString(List<Symbol> tape) {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : tape) {
            sb.append(symbol.toString());
        }
        return sb.toString();
    }
    public GrammarTypeOneEmulator(Grammar grammar) {
        this.grammar = grammar;
    }
    //suppose that input is already on the tape.
    public Pair<List<Integer>, String> emulatePartially(List<Symbol> tape) throws IOException {
        ArrayList<Integer> usedProductions = new ArrayList<>();
        BufferedWriter derivationHistory = new BufferedWriter(new FileWriter("derivation_grammar1.txt"));
        int productionStartInd = 0;
        while (true) {
            int prodInd = performFirstFromStartProduction(tape, productionStartInd);

            if (prodInd == -1) {
                derivationHistory.close();
                if (TuringMachine.tapeContainsNonTerminal(tape, grammar.terminals)) {
                    return null;
                }
                return new Pair<>(usedProductions, tapeToString(tape));
            }

            derivationHistory.write(tapeToString(tape) + "\n");
            usedProductions.add(prodInd);
        }
    }

    private int performFirstFromStartProduction(List<Symbol> input, int productionStartInd) {
        for (int i = productionStartInd; i < grammar.productions.size(); i++) {
            List<Symbol> left = grammar.productions.get(i).left;
            List<Symbol> right = grammar.productions.get(i).right;
            int ind = Collections.indexOfSubList(input, left);
            if (ind < 0) continue;
            if (!left.get(0).equals(input.get(ind))) continue;
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
}
