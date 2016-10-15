package com.formallanguages;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.formallanguages.TuringMachine.parseWholeFileToTuringMachine;

/**
 * Created by Alex on 15.10.2016.
 */
public class EmulatingGrammar0Test {
    private BufferedReader getBufferedReader(String filename) throws FileNotFoundException {
        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        return new BufferedReader(isr);
    }
    private Pair<Grammar, TuringMachine> getGrammarAndTM(String fileName) throws IOException {
        return getGrammarAndTM(fileName, true);
    }
    private Pair<Grammar, TuringMachine> getGrammarAndTM(String fileName, boolean ifTMshouldBeFlattened) throws IOException {
        BufferedReader br = getBufferedReader(fileName);
        TuringMachine machine = parseWholeFileToTuringMachine(br);
        if (ifTMshouldBeFlattened) machine.flatten();
        return new Pair<>(TuringMachineToGrammarConvertor.TuringMachineToGrammar0(machine),
                machine);
    }

    @Test
    public void emulateGrammarSimple() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("test2.jff", false);
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        CompositeSymbol epsBlank = new CompositeSymbol("eps", "blank");
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                new CompositeSymbol("1", "1"), new CompositeSymbol("0", "0"), new CompositeSymbol("1", "1"))
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = grammar.emulateGrammar0Partially(input, 10);
        Assert.assertEquals(res.snd, "101");
        System.out.println(res.snd);
    }

    @Test
    public void emulateGrammarWithInputThatBreaksIt() throws IOException {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("MTforPrimes.xml");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        CompositeSymbol epsBlank = new CompositeSymbol("eps", "blank");
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                new CompositeSymbol("1", "1"), new CompositeSymbol("0", "0"),
                new CompositeSymbol("1", "1"), new CompositeSymbol("0", "0"))
                .collect(Collectors.toList());
        try {
            Pair<List<Integer>, String> res = grammar.emulateGrammar0Partially(input, 100);
        }
        catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "Grammar cannot produce string of terminals from given input");
        }
    }

    @Test
    public void emulateGrammar0WithGoodInput() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("MTforPrimes.xml");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        CompositeSymbol epsBlank = new CompositeSymbol("eps", "blank");
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                new CompositeSymbol("1", "1"), new CompositeSymbol("0", "0"),
                new CompositeSymbol("1", "1"))
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = grammar.emulateGrammar0Partially(input, 15);
        System.out.println(res.snd);
        System.out.println(res.fst);
    }

    @Test
    public void emulateGrammar0WithAnotherGoodInput() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("MTforPrimes.xml");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        CompositeSymbol epsBlank = new CompositeSymbol("eps", "blank");
        CompositeSymbol one = new CompositeSymbol("1", "1");
        CompositeSymbol zero = new CompositeSymbol("0", "0");
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                one, zero, zero, zero,
                one)
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = grammar.emulateGrammar0Partially(input, 30);
        System.out.println(res.snd);
        System.out.println(res.fst);
    }
    @Test
    public void unrollInnerStatesAndEmulateSimpleGoodInput() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("test2.jff");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        CompositeSymbol epsBlank = new CompositeSymbol("eps", "blank");
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                new CompositeSymbol("1", "1"), new CompositeSymbol("0", "0"),
                new CompositeSymbol("1", "1"))
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = grammar.emulateGrammar0Partially(input, 1);
        Assert.assertEquals(res.snd, "101");
        System.out.println(res.snd);
        System.out.println(res.fst);
    }
    @Test
    public void unrollInnerStatesAndEmulateSimpleBadInput() throws Exception {
        Pair<Grammar, TuringMachine> grammarAndTM = getGrammarAndTM("test2.jff");
        Grammar grammar = grammarAndTM.fst;
        TuringMachine machine = grammarAndTM.snd;

        CompositeSymbol epsBlank = new CompositeSymbol("eps", "blank");
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(machine.initialState.name),
                new CompositeSymbol("blank", "blank"))
                .collect(Collectors.toList());
        try {
            Pair<List<Integer>, String> res = grammar.emulateGrammar0Partially(input, 5);
        }
        catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "Grammar cannot produce string of terminals from given input");
        }
    }
}
