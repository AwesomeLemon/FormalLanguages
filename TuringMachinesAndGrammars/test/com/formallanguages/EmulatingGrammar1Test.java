package com.formallanguages;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.formallanguages.SpecialTuringMachineSymbols.*;

/**
 * Created by Alex on 11.12.2016.
 */
public class EmulatingGrammar1Test {
    private BufferedReader getBufferedReader(String filename) throws FileNotFoundException {
        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        return new BufferedReader(isr);
    }
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
        List<Symbol> input = Stream.of(
                new ComplexSymbol(Arrays.asList(Symbol.getSymbol(machine.initialState.name), Symbol.getSymbol(LBASTART), s1, s1)),
                new ComplexSymbol(Arrays.asList(s0, s0)),
                new ComplexSymbol(Arrays.asList(s1, s1, Symbol.getSymbol(LBAEND))))
                .collect(Collectors.toList());
        Pair<List<Integer>,String> res = new GrammarTypeOneEmulator(grammar).emulatePartially(input);
     //   Assert.assertEquals(res.snd, "101");
        System.out.println(res.snd);
    }
}
