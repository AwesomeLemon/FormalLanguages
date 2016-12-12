package com.formallanguages;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

import static com.formallanguages.TuringMachine.parseFromFile;
import static com.formallanguages.TuringMachine.parseFromJflapFile;
import static com.formallanguages.StateTuringMachine.readState;
import static com.formallanguages.TuringMachine.parseWholeJflapFileToTuringMachine;

/**
 * Created by Alex on 02.10.2016.
 */

public class ReadingTuringMachineTest {
    private BufferedReader getBufferedReader(String filename) throws FileNotFoundException {
        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        return new BufferedReader(isr);
    }

    @Test
    public void readStateTest() throws IOException {
        BufferedReader br = getBufferedReader("blocksOnly.txt");
        for (int i = 0; i < 5; i++) {
            StateTuringMachine bl = readState(br);
            System.out.println(bl);
        }
        //it works if it doesn't fail.
    }

    @Test
    public void readTransitionTest() throws IOException {
        BufferedReader br = getBufferedReader("transitionsOnly.txt");
        for (int i = 0; i < 5; i++) {
            TransitionTuringMachine tr = TransitionTuringMachine.readTransitionJflap(br);
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
        TuringMachine dec = parseFromJflapFile(br, "dec.jff");
        System.out.println(dec);
        //it works if it doesn't fail.
    }

    @Test
    public void readSeveralSimpleTuringMachines() throws IOException {
        BufferedReader br = getBufferedReader("decCloneShort.txt");
        TuringMachine turingMachine = parseFromJflapFile(br, "automaton");
        System.out.println(turingMachine);
        //it works if it doesn't fail.
    }

    @Test
    public void readTuringMachineWithInnerTuringMachines() throws IOException {
        BufferedReader br = getBufferedReader("checkIfPrimeTM_short.txt");
        TuringMachine turingMachine = parseFromJflapFile(br, "automaton");
        System.out.println(turingMachine);
        //it works if it doesn't fail.
    }

    @Test
    public void readTuringMachineWithInnerTuringMachinesWholeFile() throws IOException {
        BufferedReader br = getBufferedReader("checkIfPrimeTM.xml");
        TuringMachine turingMachine = parseWholeJflapFileToTuringMachine(br);
        System.out.println(turingMachine);
        //it works if it doesn't fail.
    }

    @Test
    public void convertSimpleTuringMachineToGrammar0() throws IOException {
        BufferedReader br = getBufferedReader("dec.txt");
        TuringMachine dec = parseFromJflapFile(br, "dec.jff");
        Grammar decGrammar = TuringMachineToGrammarConvertor.toType0Grammar(dec);
        System.out.println(decGrammar);
        //it works if it doesn't fail.
    }

    @Test
    public void unrollInnerStates() throws IOException {
        BufferedReader br = getBufferedReader("checkIfPrimeTM.jff");
        TuringMachine machine = parseWholeJflapFileToTuringMachine(br);
        machine.flatten();
        System.out.println(machine);
    }

    @Test
    public void convertLba() throws IOException {
        BufferedReader br = getBufferedReader("lba_primes.txt");
        TuringMachine machine = parseFromFile(br);
        System.out.println(machine);
    }
}
