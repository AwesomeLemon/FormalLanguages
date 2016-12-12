package com.formallanguages;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public Pair<List<Integer>, String> emulatePartially(List<Symbol> tape, int maxTapeCount) throws IOException {
        DoubleSymbol epsBlankSym = DoubleSymbol.getDoubleSymbol(EPSILON, BLANK);
        ArrayList<Integer> usedProductions = new ArrayList<>();
        for (int i = 0; i < maxTapeCount; i++) {
            tape.add(epsBlankSym);
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter("derivation_grammar0.txt"));
        int productionStartInd = findFirst6thTypeProduction();
        while (true) {
            int prodInd = performFirstFromStartProduction(tape, productionStartInd);

            if (prodInd == -1) {
                TuringMachine.tapeRemoveEpsilons(tape);
                bw.close();
                if (TuringMachine.tapeContainsNonTerminal(tape, grammar.terminals)) {
                    return null;
                }
                return new Pair<>(usedProductions, tapeToString(tape));
            }

            bw.write(tapeToString(tape) + "\n\n");

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

    private String otherTapeToString(List<Symbol> tape) {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : tape) {
            if (symbol.value.contains("q")) sb.append("(").append(symbol.value).append(")");
            if (symbol instanceof DoubleSymbol) {
                String sym = ((DoubleSymbol) symbol).value2;
                if (sym.equals(BLANK)) {
                    sb.append("_");
                    continue;
                }
                if (!sym.equals(EPSILON)) sb.append(sym);
            }
        }
        return sb.toString();
    }
//    private String tapeToString(List<Symbol> tape) {
//        StringBuilder sb = new StringBuilder();
//        for (Symbol symbol : tape) {
//            sb.append(symbol.toString());
//        }
//        return sb.toString();
//    }
    private String tapeToString(List<Symbol> tape) {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : tape) {
            if (!symbol.value.equals(EPSILON)) sb.append(symbol.value);
        }
        return sb.toString();
    }
}
