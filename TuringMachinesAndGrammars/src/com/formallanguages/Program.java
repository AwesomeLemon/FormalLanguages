package com.formallanguages;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.formallanguages.SpecialTuringMachineSymbols.BLANK;
import static com.formallanguages.SpecialTuringMachineSymbols.EPSILON;

/**
 * Created by Alex on 12.12.2016.
 */
public class Program {
    public static void main(String[] args) {
//       if (args[0].equals("0")) {
            String filename = "checkIfPrimeTM.jff";
            try {
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
            }
            catch (IOException e) {
                System.out.println("Probably, file is not found: " + filename);
                e.printStackTrace();
            }
//        }
    }
}
