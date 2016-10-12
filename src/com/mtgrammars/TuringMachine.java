package com.mtgrammars;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import static com.mtgrammars.BlockTuringMachine.readBlock;
import static com.mtgrammars.TransitionTuringMachine.readTransition;

/**
 * Created by Alex on 07.10.2016.
 */
public class TuringMachine {
    final ArrayList<BlockTuringMachine> blocks;
    final ArrayList<TransitionTuringMachine> transitions;
    final HashMap<String, TuringMachine> associatedMTs;
    TreeSet<String> inputAlphabet;
    TreeSet<String> tapeAlphabet;
    BlockTuringMachine initialState;
    ArrayList<BlockTuringMachine> finalStates;

    public TuringMachine(ArrayList<BlockTuringMachine> blocks, ArrayList<TransitionTuringMachine> transitions, HashMap<String, TuringMachine> associatedMTs) {
        this.blocks = blocks;
        this.transitions = transitions;
        this.associatedMTs = associatedMTs;

        inputAlphabet = new TreeSet<>();
        for (TransitionTuringMachine transition : transitions) {
            inputAlphabet.add(transition.read);
            inputAlphabet.add(transition.write);
        }
        if (inputAlphabet.contains("blank")) inputAlphabet.remove("blank");

        tapeAlphabet = new TreeSet<>(inputAlphabet);
        tapeAlphabet.add("blank");

        finalStates = new ArrayList<>();
        for (BlockTuringMachine block : blocks) {
            if (block.isFinal) {
                finalStates.add(block);
            }
            else {
                if (block.isInitial) {
                    initialState = block;
                }
            }
        }
    }

    static TuringMachine readMT(BufferedReader br, String name) throws Exception {
        HashMap<String, TuringMachine> mts = new HashMap<>();

        ArrayList<BlockTuringMachine> blocks = new ArrayList<>();
        br.readLine();//"<!--The list of states.-->"
        BlockTuringMachine block = readBlock(br);
        while (block != null) {
            blocks.add(block);
            block = readBlock(br);
        }

        ArrayList<TransitionTuringMachine> transitions = new ArrayList<>();
        TransitionTuringMachine transition = readTransition(br);
        while (transition != null) {
            transitions.add(transition);
            transition = readTransition(br);
        }

        String line;
        String closingTag = String.format("</%1$s>", name);

        while (true) {
            while ((line = br.readLine().trim()).isEmpty() && !line.equals(closingTag)) {}

            if (line.equals(closingTag)) return new TuringMachine(blocks, transitions, mts);

            String innerMTname = line.trim().substring(1, line.length() - 1);
            TuringMachine innerTuringMachine = readMT(br, innerMTname);

            mts.put(innerMTname, innerTuringMachine);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TuringMachine turingMachine = (TuringMachine) o;

        if (blocks != null ? !blocks.equals(turingMachine.blocks) : turingMachine.blocks != null) return false;
        if (transitions != null ? !transitions.equals(turingMachine.transitions) : turingMachine.transitions != null) return false;
        return associatedMTs != null ? associatedMTs.equals(turingMachine.associatedMTs) : turingMachine.associatedMTs == null;

    }

    @Override
    public int hashCode() {
        int result = blocks != null ? blocks.hashCode() : 0;
        result = 31 * result + (transitions != null ? transitions.hashCode() : 0);
        result = 31 * result + (associatedMTs != null ? associatedMTs.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TuringMachine{" +
                "blocks=" + blocks +
                ", transitions=" + transitions +
                ", associatedMTs=" + associatedMTs +
                '}';
    }
}
