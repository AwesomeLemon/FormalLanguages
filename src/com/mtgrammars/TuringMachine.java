package com.mtgrammars;

import java.io.BufferedReader;
import java.util.*;

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

    public TuringMachine(TuringMachine tm) {
        blocks = new ArrayList<>(tm.blocks.size());
        for (BlockTuringMachine block : tm.blocks) {
            blocks.add(new BlockTuringMachine(block));
        }
        transitions = new ArrayList<>(tm.transitions.size());
        for (TransitionTuringMachine transition : tm.transitions) {
            transitions.add(new TransitionTuringMachine(transition));
        }
        associatedMTs = new HashMap<>();
        for (Map.Entry<String, TuringMachine> tmachine : tm.associatedMTs.entrySet()) {
            associatedMTs.put(tmachine.getKey(), new TuringMachine(tmachine.getValue()));
        }
        inputAlphabet = (TreeSet<String>) tm.inputAlphabet.clone();
        tapeAlphabet = (TreeSet<String>) tm.tapeAlphabet.clone();

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
            //"|| line.contains("<Machine")" is a quick but dirty hack. Because of it, other than my TM may not parse.
            while (((line = br.readLine().trim()).isEmpty() || line.contains("<Machine")) && !line.contains(closingTag)) {}

            if (line.contains(closingTag)) return new TuringMachine(blocks, transitions, mts);

            String innerMTname = line.substring(line.indexOf('<') + 1, line.lastIndexOf('>'));
            TuringMachine innerTuringMachine = readMT(br, innerMTname);
            mts.put(innerMTname, innerTuringMachine);
        }
    }

    void addPrefixToAllStates(String prefix) {
        for (BlockTuringMachine block : blocks) {
            block.name = prefix + block.name;
        }
    }

    static TuringMachine readMTWholeFile(BufferedReader br) throws Exception {
        br.readLine();
        br.readLine();
        br.readLine();
        return readMT(br, "automaton");
    }

    int findNextInnerTM(int index) {
        for (int i = index; i < blocks.size(); i++) {
            if (associatedMTs.containsKey(blocks.get(i).tag)) {
                return i;
            }
        }
        return -1;
    }

    void unrollInnerTMs() {
        int nextInnerTM = findNextInnerTM(0);
        int innerTMcount = 0;
        while (nextInnerTM != -1) {
            unrollTMfromBlock(blocks.get(nextInnerTM), innerTMcount);
            innerTMcount++;
            nextInnerTM = findNextInnerTM(nextInnerTM);
        }
        associatedMTs.clear();
    }

    int findMaximumBlockId() {
        return blocks.stream().map(x -> x.id).max(Comparator.naturalOrder()).get();
    }

    void incrementAllStateIdsBy(int addThis) {
        for (BlockTuringMachine block : blocks) {
            block.id += addThis;
        }
        for (TransitionTuringMachine trans : transitions) {
            trans.from += addThis;
            trans.to += addThis;
        }
    }

    void unrollTMfromBlock(BlockTuringMachine blockThatIsTM, int uniqueUnrollingId) {
        TuringMachine clonedTM = new TuringMachine(associatedMTs.get(blockThatIsTM.tag));
        clonedTM.incrementAllStateIdsBy(findMaximumBlockId() + 1);
        blocks.remove(blockThatIsTM);
        //right part:
        ArrayList<TransitionTuringMachine> additionalTransitions = new ArrayList<>();
        for (TransitionTuringMachine trans : this.transitions) {
            if (trans.to == blockThatIsTM.id) {
                trans.to = clonedTM.initialState.id;
            }
            if (trans.from == blockThatIsTM.id) {
                trans.from = clonedTM.finalStates.get(0).id;
                for (int i = 1; i < clonedTM.finalStates.size(); i++) {
                    additionalTransitions.add(new TransitionTuringMachine(
                            clonedTM.finalStates.get(i).id, trans.to, trans.read, trans.write, trans.direction));
                }
            }
        }
        this.transitions.addAll(additionalTransitions);
        this.transitions.addAll(clonedTM.transitions);
        this.inputAlphabet.addAll(clonedTM.inputAlphabet);
        this.tapeAlphabet.addAll(clonedTM.tapeAlphabet);
        clonedTM.addPrefixToAllStates(blockThatIsTM.name + "_" + uniqueUnrollingId + "_");
        this.blocks.addAll(clonedTM.blocks);
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