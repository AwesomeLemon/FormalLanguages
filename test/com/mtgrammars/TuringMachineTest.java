package com.mtgrammars;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mtgrammars.TuringMachine.readMT;
import static com.mtgrammars.BlockTuringMachine.readBlock;

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
    public void readBlockTest() throws Exception {
        BufferedReader br = getBufferedReader("block.txt");
        for (int i = 0; i < 5; i++) {
            BlockTuringMachine bl = readBlock(br);
            System.out.println(bl);
        }
    }

    @Test
    public void readTransitionTest() throws Exception {
        BufferedReader br = getBufferedReader("trans.txt");
        for (int i = 0; i < 5; i++) {
            TransitionTuringMachine tr = TransitionTuringMachine.readTransition(br);
            System.out.println(tr);
        }
    }

    @Test
    public void parseEmptyClosingTagTest() throws Exception {
        Assert.assertEquals(ParsingXMLUtilities.parseEmptyClosingTag("</dec.jff>"), "dec.jff");
    }

    @Test
    public void readDecMT() throws Exception {
        BufferedReader br = getBufferedReader("dec.txt");
        TuringMachine dec = readMT(br, "dec.jff");
        System.out.println(dec);
    }

    @Test
    public void readDecAndCloneLRMT() throws Exception {
        BufferedReader br = getBufferedReader("decCloneShort.txt");
        TuringMachine dec = readMT(br, "automaton");
        System.out.println(dec);
    }

    @Test
    public void readFullMT() throws Exception {
        BufferedReader br = getBufferedReader("fullMT.txt");
        TuringMachine dec = readMT(br, "automaton");
        System.out.println(dec);
    }

    @Test
    public void convertMTtoGrammar() throws Exception {
        BufferedReader br = getBufferedReader("dec.txt");
        TuringMachine dec = readMT(br, "dec.jff");
        Grammar decGrammar = TuringMachineConvertor.TuringMachineToGrammar0(dec);
        System.out.println(decGrammar);
    }

    @Test
    public void emulateGrammarDec() throws Exception {
        BufferedReader br = getBufferedReader("dec.txt");
        TuringMachine dec = readMT(br, "dec.jff");
        Grammar decGrammar = TuringMachineConvertor.TuringMachineToGrammar0(dec);
        CompositeSymbol epsBlank = new CompositeSymbol("eps", "blank");
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(dec.initialState.name), new CompositeSymbol("a", "a"),
                new CompositeSymbol("1", "1"), new CompositeSymbol("0", "0"), new CompositeSymbol("a", "a"))
                .collect(Collectors.toList());
        List<Integer> res = decGrammar.emulateGrammar0Partially(input, 10);
        for (Integer i : res) {
            System.out.println(decGrammar.productions.get(i));
        }
        System.out.println(res);
    }

    @Test
    public void emulateGrammar() throws Exception {
        BufferedReader br = getBufferedReader("fullMT.txt");
        TuringMachine dec = readMT(br, "automaton");
        Grammar decGrammar = TuringMachineConvertor.TuringMachineToGrammar0(dec);
        CompositeSymbol epsBlank = new CompositeSymbol("eps", "blank");
        List<Symbol> input = Stream.of(epsBlank, epsBlank, epsBlank, new Symbol(dec.initialState.name),
                new CompositeSymbol("1", "1"), new CompositeSymbol("0", "0"), new CompositeSymbol("1", "1"))
                .collect(Collectors.toList());
        List<Integer> res = decGrammar.emulateGrammar0Partially(input, 100);
        for (Integer i : res) {
            System.out.println(decGrammar.productions.get(i));
        }
        System.out.println(res);
    }
}
