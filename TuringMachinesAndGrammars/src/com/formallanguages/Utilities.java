package com.formallanguages;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by Alex on 12.12.2016.
 */
public class Utilities {
    static BufferedReader getBufferedReader(String filename) throws FileNotFoundException {
        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        return new BufferedReader(isr);
    }

    static void writeGrammar(String filename, Grammar grammar) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
            writer.write("Terminals:\n");
            for (Symbol terminal : grammar.terminals) {
                writer.write(terminal + "; ");
            }
            writer.write("\nNonterminals:\n");
            for (Symbol nonterminal : grammar.nonterminals) {
                writer.write(nonterminal + "; ");
            }
            writer.write("\nAxiom: " + grammar.axiom.toString() + "\n");
            for (Production production : grammar.productions) {
                writer.write(production.toString() + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
