package com.mtgrammars;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mtgrammars.TuringMachine.parseFromFile;
import static com.mtgrammars.StateTuringMachine.readState;
import static com.mtgrammars.TuringMachine.parseWholeFileToTuringMachine;

/**
 * Created by Alex on 02.10.2016.
 */

public class TuringMachineTest {
    private BufferedReader getBufferedReader(String filename) throws FileNotFoundException {
        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        return new BufferedReader(isr);
    }

    @Test
    public void readStateTest() throws IOException {
        BufferedReader br = getBufferedReader("block.txt");
        for (int i = 0; i < 5; i++) {
            StateTuringMachine bl = readState(br);
            System.out.println(bl);
        }
        //it works if it doesn't fail.
    }

    @Test
    public void readTransitionTest() throws IOException {
        BufferedReader br = getBufferedReader("trans.txt");
        for (int i = 0; i < 5; i++) {
            TransitionTuringMachine tr = TransitionTuringMachine.readTransition(br);
            System.out.println(tr);
        }
        //it works if it doesn't fail.
    }

    @Test
    public void parseEmptyClosingTagTest() {
        Assert.assertEquals(ParsingXMLUtilities.parseEmptyClosingTag("</dec.jff>"), "dec.jff");
    }

    @Test
    public void readSimpleTuringMachine() throws IOException {
        BufferedReader br = getBufferedReader("dec.txt");
        TuringMachine dec = parseFromFile(br, "dec.jff");
        System.out.println(dec);
        //it works if it doesn't fail.
    }

    @Test
    public void readSeveralSimpleTuringMachines() throws IOException {
        BufferedReader br = getBufferedReader("decCloneShort.txt");
        TuringMachine turingMachine = parseFromFile(br, "automaton");
        System.out.println(turingMachine);
        //it works if it doesn't fail.
    }

    @Test
    public void readTuringMachineWithInnerTuringMachines() throws IOException {
        BufferedReader br = getBufferedReader("fullMT.txt");
        TuringMachine turingMachine = parseFromFile(br, "automaton");
        System.out.println(turingMachine);
        //it works if it doesn't fail.
    }

    @Test
    public void readTuringMachineWithInnerTuringMachinesWholeFile() throws IOException {
        BufferedReader br = getBufferedReader("MTforPrimes.xml");
        TuringMachine turingMachine = parseWholeFileToTuringMachine(br);
        System.out.println(turingMachine);
        //it works if it doesn't fail.
    }

    @Test
    public void convertSimpleTuringMachineToGrammar0() throws IOException {
        BufferedReader br = getBufferedReader("dec.txt");
        TuringMachine dec = parseFromFile(br, "dec.jff");
        Grammar decGrammar = TuringMachineToGrammarConvertor.TuringMachineToGrammar0(dec);
        System.out.println(decGrammar);
        //it works if it doesn't fail.
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
    public void unrollInnerStates() throws IOException {
        BufferedReader br = getBufferedReader("MTforPrimes.xml");
        TuringMachine machine = parseWholeFileToTuringMachine(br);
        machine.flatten();
        System.out.println(machine);
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
