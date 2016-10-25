package com.formallanguages;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.formallanguages.CompositeSymbol.getCompositeSymbol;
import static com.formallanguages.TuringMachine.otherTapeToString;
import static com.formallanguages.SpecialTuringMachineSymbols.*;

/** Emulates type zero grammar, generated from Turing Machine using algorithm,
 * described in Martynenko's "Languages and translations". (ISBN 5-288-02870-2)
 */
public class GrammarTypeZeroEmulator {
    private final Grammar grammar;

    public GrammarTypeZeroEmulator(Grammar grammar) {
        this.grammar = grammar;
    }

    //emulates starting after (5): A3 -> eps.
    public Pair<List<Integer>, String> emulatePartially(List<Symbol> tape, int maxTapeCount) throws Exception {
        CompositeSymbol epsBlankSym = getCompositeSymbol(EPSILON, BLANK);
        ArrayList<Integer> usedProductions = new ArrayList<>();
        for (int i = 0; i < maxTapeCount; i++) {
            tape.add(epsBlankSym);
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));
        int productionStartInd = findFirst6thTypeProduction();
        while (true) {
            int prodInd = performFirstFromStartProduction(tape, productionStartInd);

            if (prodInd == -1) {
                TuringMachine.tapeRemoveEpsilons(tape);
                bw.close();
                if (TuringMachine.tapeContainsNonTerminal(tape, grammar.terminals)) {
                    throw new Exception("Grammar cannot produce string of terminals from given input");
                    //grammar cannot process given input, 'cause corresponding TM doesn't accept it.
                    //or the grammar is wrong.
                }
                return new Pair<>(usedProductions, TuringMachine.tapeToString(tape));
            }

            bw.write(grammar.productions.get(prodInd) + ": " + otherTapeToString(tape) + "\n");
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

    private int findFirst6thTypeProduction() {
        Symbol eps = Symbol.getSymbol(EPSILON);
        for (int i = 0; i < grammar.productions.size(); i++) {
            if (grammar.productions.get(i).right.size() > 1) continue;
            if (grammar.productions.get(i).right.get(0).equals(eps)) return i + 1;
        }
        return -1;
    }
}
