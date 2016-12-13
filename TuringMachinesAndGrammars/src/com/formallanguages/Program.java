package com.formallanguages;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.formallanguages.SpecialTuringMachineSymbols.*;

/**
 * Created by Alex on 12.12.2016.
 */
public class Program {
    public static void main(String[] args) {
            try {
                String filename = ".\\checkIfPrimeTM.jff";
                TuringMachine turingMachine = TuringMachine.parseWholeJflapFileToTuringMachine(Utilities.getBufferedReader(filename));
                turingMachine.flatten();
                Grammar grammar = TuringMachineToGrammarConvertor.toType0Grammar(turingMachine);
                Utilities.writeGrammar("grammar0.txt", grammar);
                GrammarTypeZeroEmulator grammarTypeZeroEmulator = new GrammarTypeZeroEmulator(grammar);

                DoubleSymbol epsBlank = DoubleSymbol.getDoubleSymbol(EPSILON, BLANK);
                List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(turingMachine.initialState.name),
                        DoubleSymbol.getDoubleSymbol("1", "1"), DoubleSymbol.getDoubleSymbol("0", "0"),
                        DoubleSymbol.getDoubleSymbol("1", "1"))
                        .collect(Collectors.toList());
                grammarTypeZeroEmulator.emulatePartially(input, 15);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String filename = ".\\lba_primes.txt";
                TuringMachine turingMachine = TuringMachine.parseFromFile(Utilities.getBufferedReader(filename));
                Grammar grammar = TuringMachineToGrammarConvertor.toType1Grammar(turingMachine);
                Utilities.writeGrammar("grammar1.txt", grammar);
                GrammarTypeOneEmulator grammarTypeOneEmulator = new GrammarTypeOneEmulator(grammar);

                Symbol s1 = Symbol.getSymbol("1");
                Symbol s0 = Symbol.getSymbol("0");
                List<Symbol> input5 = Stream.of(
                        new ComplexSymbol(Arrays.asList(Symbol.getSymbol(turingMachine.initialState.name), Symbol.getSymbol(LBASTART), s1, s1)),
                        new ComplexSymbol(Arrays.asList(s0, s0)),
                        new ComplexSymbol(Arrays.asList(s1, s1, Symbol.getSymbol(LBAEND))))
                        .collect(Collectors.toList());
                grammarTypeOneEmulator.emulatePartially(input5);

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
