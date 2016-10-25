package com.formallanguages;

import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

import static com.formallanguages.TuringMachine.parseFromFile;
/**
 * Created by Alex on 15.10.2016.
 */
public class ConvertingTuringMachineToGrammarTest {
    private BufferedReader getBufferedReader(String filename) throws FileNotFoundException {
        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        return new BufferedReader(isr);
    }
    @Test
    public void convertSimpleTuringMachineToGrammar0() throws IOException {
        BufferedReader br = getBufferedReader("dec.txt");
        TuringMachine dec = parseFromFile(br, "dec.jff");
        Grammar decGrammar = TuringMachineToGrammarConvertor.toType0Grammar(dec);
        System.out.println(decGrammar);
        //it works if it doesn't fail.
    }
}
