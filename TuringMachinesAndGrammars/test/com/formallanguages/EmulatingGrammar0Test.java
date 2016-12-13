package com.formallanguages;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static com.formallanguages.SpecialTuringMachineSymbols.*;
import static com.formallanguages.TuringMachine.parseWholeJflapFileToTuringMachine;
import static com.formallanguages.Utilities.getBufferedReader;

/**
 * Created by Alex on 15.10.2016.
 */
public class EmulatingGrammar0Test {

    private Pair<Grammar, TuringMachine> getGrammarAndTM(String fileName) throws IOException {
        return getGrammarAndTM(fileName, true);
    }
    private Pair<Grammar, TuringMachine> getGrammarAndTM(String fileName, boolean ifTMshouldBeFlattened) throws IOException {
        BufferedReader br = getBufferedReader(fileName);
        TuringMachine machine = parseWholeJflapFileToTuringMachine(br);
        if (ifTMshouldBeFlattened) machine.flatten();
        return new Pair<>(TuringMachineToGrammarConvertor.toType0Grammar(machine),
                machine);
    }

    @Test
    public void emulateGrammarSimple() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("flipTwoBytesTM.jff", false);
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        DoubleSymbol epsBlank = DoubleSymbol.getDoubleSymbol(EPSILON, BLANK);
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                DoubleSymbol.getDoubleSymbol("1", "1"), DoubleSymbol.getDoubleSymbol("0", "0"), DoubleSymbol.getDoubleSymbol("1", "1"))
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = new GrammarTypeZeroEmulator(grammar).emulatePartially(input, 10);
        Assert.assertEquals(res.snd, "{1}{0}{1}");
        System.out.println(res.snd);
    }

    @Test
    public void emulateGrammarWithInputThatBreaksIt() throws IOException {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("checkIfPrimeTM.jff");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        DoubleSymbol epsBlank = DoubleSymbol.getDoubleSymbol(EPSILON, BLANK);
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                DoubleSymbol.getDoubleSymbol("1", "1"), DoubleSymbol.getDoubleSymbol("0", "0"),
                DoubleSymbol.getDoubleSymbol("1", "1"), DoubleSymbol.getDoubleSymbol("0", "0"))
                .collect(Collectors.toList());
        try {
            Pair<List<Integer>, String> res = new GrammarTypeZeroEmulator(grammar).emulatePartially(input, 100);
        }
        catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "Grammar cannot produce string of terminals from given input");
        }
    }

    @Test
    public void emulateGrammar0WithGoodInput() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("checkIfPrimeTM.jff");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        DoubleSymbol epsBlank = DoubleSymbol.getDoubleSymbol(EPSILON, BLANK);
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                DoubleSymbol.getDoubleSymbol("1", "1"), DoubleSymbol.getDoubleSymbol("0", "0"),
                DoubleSymbol.getDoubleSymbol("1", "1"))
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = new GrammarTypeZeroEmulator(grammar).emulatePartially(input, 15);
        System.out.println(res.snd);
        System.out.println(res.fst);
    }

    @Test
    public void emulateGrammar0WithAnotherGoodInput() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("checkIfPrimeTM.jff");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        DoubleSymbol epsBlank = DoubleSymbol.getDoubleSymbol(EPSILON, BLANK);
        DoubleSymbol one = DoubleSymbol.getDoubleSymbol("1", "1");
        DoubleSymbol zero = DoubleSymbol.getDoubleSymbol("0", "0");
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                one, one, zero,
                one)
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = new GrammarTypeZeroEmulator(grammar).emulatePartially(input, 30);
        System.out.println(res.snd);
        System.out.println(res.fst);
    }
    @Test
    public void unrollInnerStatesAndEmulateSimpleGoodInput() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("flipTwoBytesTM.jff");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        DoubleSymbol epsBlank = DoubleSymbol.getDoubleSymbol(EPSILON, BLANK);
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                DoubleSymbol.getDoubleSymbol("1", "1"), DoubleSymbol.getDoubleSymbol("0", "0"),
                DoubleSymbol.getDoubleSymbol("1", "1"))
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = new GrammarTypeZeroEmulator(grammar).emulatePartially(input, 1);
        Assert.assertEquals(res.snd, "{1}{0}{1}");
        System.out.println(res.snd);
        System.out.println(res.fst);
    }
    @Test
    public void unrollInnerStatesAndEmulateSimpleBadInput() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("flipTwoBytesTM.jff");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        DoubleSymbol epsBlank = DoubleSymbol.getDoubleSymbol(EPSILON, BLANK);
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                DoubleSymbol.getDoubleSymbol(BLANK, BLANK))
                .collect(Collectors.toList());
        try {
            Pair<List<Integer>, String> res = new GrammarTypeZeroEmulator(grammar).emulatePartially(input, 5);
        }
        catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "Grammar cannot produce string of terminals from given input");
        }
    }
}
