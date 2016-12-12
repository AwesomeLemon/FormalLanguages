package com.formallanguages;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.formallanguages.SpecialTuringMachineSymbols.*;
import static com.formallanguages.Utilities.getBufferedReader;

/**
 * Created by Alex on 11.12.2016.
 */
public class EmulatingGrammar1Test {
    private Pair<Grammar, TuringMachine> getGrammarAndTM(String fileName) throws IOException {
        BufferedReader br = getBufferedReader(fileName);
        TuringMachine machine = TuringMachine.parseFromFile(br);
        return new Pair<>(TuringMachineToGrammarConvertor.toType1Grammar(machine),
                machine);
    }

    @Test
    public void emulateGrammarSimple() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("lba_primes.txt");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        Symbol s1 = Symbol.getSymbol("1");
        Symbol s0 = Symbol.getSymbol("0");
        List<Symbol> input5 = Stream.of(
                new ComplexSymbol(Arrays.asList(Symbol.getSymbol(machine.initialState.name), Symbol.getSymbol(LBASTART), s1, s1)),
                new ComplexSymbol(Arrays.asList(s0, s0)),
                new ComplexSymbol(Arrays.asList(s1, s1, Symbol.getSymbol(LBAEND))))
                .collect(Collectors.toList());
        List<Symbol> input2 = Stream.of(
                new ComplexSymbol(Arrays.asList(Symbol.getSymbol(machine.initialState.name), Symbol.getSymbol(LBASTART), s1, s1)),
                new ComplexSymbol(Arrays.asList(s0, s0, Symbol.getSymbol(LBAEND))))
                .collect(Collectors.toList());
        List<Symbol> input1 = Stream.of(
                new ComplexSymbol(Arrays.asList(Symbol.getSymbol(machine.initialState.name), Symbol.getSymbol(LBASTART), s1, s1, Symbol.getSymbol(LBAEND))))
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = new GrammarTypeOneEmulator(grammar).emulatePartially(input5);
     //   Assert.assertEquals(res.snd, "101");
        System.out.println(res.snd);
    }
}
