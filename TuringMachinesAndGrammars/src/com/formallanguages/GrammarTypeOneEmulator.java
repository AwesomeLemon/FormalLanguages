package com.formallanguages;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.formallanguages.TuringMachine.otherTapeToString_forGrammar0;

/**
 * Created by Alex on 11.12.2016.
 */
public class GrammarTypeOneEmulator {
    private final Grammar grammar;

    public GrammarTypeOneEmulator(Grammar grammar) {
        this.grammar = grammar;
    }
    //suppose that input is already on the tape.
    public Pair<List<Integer>, String> emulatePartially(List<Symbol> tape) throws Exception{
        ArrayList<Integer> usedProductions = new ArrayList<>();
        BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));
        int productionStartInd = 0;//temporary
        while (true) {
            int prodInd = performFirstFromStartProduction(tape, productionStartInd);

            if (prodInd == -1) {
                bw.close();
                if (TuringMachine.tapeContainsNonTerminal(tape, grammar.terminals)) {
                    throw new Exception("Grammar cannot produce string of terminals from given input");
                    //grammar cannot process given input, 'cause corresponding TM doesn't accept it.
                    //or the grammar is wrong.
                }
                return new Pair<>(usedProductions, TuringMachine.tapeToString_forGrammar0(tape));
            }

            bw.write(grammar.productions.get(prodInd) + ": " + TuringMachine.tapeToString_forGrammar1(tape) + "\n");
            usedProductions.add(prodInd);
        }
    }

    private int performFirstFromStartProduction(List<Symbol> input, int productionStartInd) {
        for (int i = productionStartInd; i < grammar.productions.size(); i++) {
            List<Symbol> left = grammar.productions.get(i).left;
            List<Symbol> right = grammar.productions.get(i).right;
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
}
