package com.formallanguages;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static com.formallanguages.SpecialTuringMachineSymbols.*;
import static com.formallanguages.StateTuringMachine.readState;
import static com.formallanguages.TransitionTuringMachine.readTransitionJflap;

/**
 * Created by Alex on 07.10.2016.
 */
public class TuringMachine {
    final ArrayList<StateTuringMachine> blocks;
    final ArrayList<TransitionTuringMachine> transitions;
    private final HashMap<String, TuringMachine> associatedMTs;
    HashSet<String> inputAlphabet;
    HashSet<String> tapeAlphabet;
    StateTuringMachine initialState;
    ArrayList<StateTuringMachine> finalStates;

    public void makeStateInitial(String initialName) {
        StateTuringMachine initial = blocks.stream()
                .filter(stateTuringMachine -> Objects.equals(stateTuringMachine.name, initialName)).findFirst().get();
        initial.isInitial = true;
        initialState = initial;
    }

    public void makeStateFinal(String finalName) {
        StateTuringMachine finalState = blocks.stream()
                .filter(stateTuringMachine -> Objects.equals(stateTuringMachine.name, finalName)).findFirst().get();
        finalState.isFinal = true;
        finalStates.add(finalState);
    }

    //copy constructor
    public TuringMachine(TuringMachine turingMachine) {
        blocks = new ArrayList<>(turingMachine.blocks.size());
        for (StateTuringMachine block : turingMachine.blocks) {
            blocks.add(new StateTuringMachine(block));
        }
        
        transitions = new ArrayList<>(turingMachine.transitions.size());
        for (TransitionTuringMachine transition : turingMachine.transitions) {
            transitions.add(new TransitionTuringMachine(transition));
        }
        
        associatedMTs = new HashMap<>();
        for (Map.Entry<String, TuringMachine> tmachine : turingMachine.associatedMTs.entrySet()) {
            associatedMTs.put(tmachine.getKey(), new TuringMachine(tmachine.getValue()));
        }
        
        inputAlphabet = (HashSet<String>) turingMachine.inputAlphabet.clone();
        tapeAlphabet = (HashSet<String>) turingMachine.tapeAlphabet.clone();

        finalStates = new ArrayList<>();
        for (StateTuringMachine block : blocks) {
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

    public TuringMachine(ArrayList<StateTuringMachine> blocks, ArrayList<TransitionTuringMachine> transitions, HashMap<String, TuringMachine> associatedMTs) {
        this.blocks = blocks;
        this.transitions = transitions;
        this.associatedMTs = associatedMTs;
        inputAlphabet = new HashSet<>();
        //inputAlphabet cannot be constructed perfectly from the TM without emulating it.
        for (TransitionTuringMachine transition : transitions) {
            inputAlphabet.add(transition.read);
            inputAlphabet.add(transition.write);
        }
        if (inputAlphabet.contains(BLANK)) inputAlphabet.remove(BLANK);

        tapeAlphabet = new HashSet<>(inputAlphabet);
        tapeAlphabet.add(BLANK);

        finalStates = new ArrayList<>();
        for (StateTuringMachine block : blocks) {
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

    static TuringMachine parseFromFile(BufferedReader br) throws IOException {
        HashSet<StateTuringMachine> states = new HashSet<>();
        HashSet<TransitionTuringMachine> transitions = new HashSet<>();
        HashSet<String> variableValues = new HashSet<>(Arrays.asList("0", "1", "b"));//hardcoded, sorry.
        String line;
        while (!(line = br.readLine()).equals("Productions end")) {
            if (line.isEmpty()) continue;
            String[] tokens = line.split(",");
            String fromName = tokens[0];
            int fromId = Integer.parseInt(fromName.substring(1));
            String toName = tokens[1];
            int toId = Integer.parseInt(toName.substring(1));
            StateTuringMachine fromState = new StateTuringMachine(fromName, fromId, "", false, false);
            StateTuringMachine toState = new StateTuringMachine(toName, toId, "", false, false);
            states.add(fromState);
            states.add(toState);
            String read = tokens[2];
            String write = tokens[3];
            TransitionTuringMachine.Direction direction = tokens[4].equals("r") ? TransitionTuringMachine.Direction.Right
                    : TransitionTuringMachine.Direction.Left;
            if (read.equals("id")) read = "[x;y;z]";
            if (write.equals("id")) write = "[x;y;z]";
            if (read.contains("x")) {
                if (read.contains("y")) {
                    if (read.contains("z")) {
                        for (String xvar : variableValues) {
                            for (String yvar : variableValues) {
                                for (String zvar : variableValues) {
                                    transitions.add(new TransitionTuringMachine(fromId, toId,
                                            read.replace("x", xvar).replace("y", yvar).replace("z", zvar),
                                            write.replace("x", xvar).replace("y", yvar).replace("z", zvar), direction));
                                }
                            }
                        }
                    }
                    else {
                        for (String xvar : variableValues) {
                            for (String yvar : variableValues) {
                                transitions.add(new TransitionTuringMachine(fromId, toId,
                                        read.replace("x", xvar).replace("y", yvar),
                                        write.replace("x", xvar).replace("y", yvar), direction));
                            }
                        }
                    }
                }
                else {
                    for (String xvar : variableValues) {
                        transitions.add(new TransitionTuringMachine(fromId, toId, read.replace("x", xvar),
                                write.replace("x", xvar), direction));
                    }
                }
            }//suppose that if there's 'y', there's 'x'.
            else {
                TransitionTuringMachine transition = new TransitionTuringMachine(fromId, toId, read, write, direction);
                transitions.add(transition);
            }
        }
        TuringMachine turingMachine = new TuringMachine(new ArrayList<>(states), new ArrayList<>(transitions), new HashMap<>());
        turingMachine.correctInputAlphabetLba();
        turingMachine.makeStateInitial(br.readLine());
        turingMachine.inputAlphabet = new HashSet<>(Arrays.asList("0", "1"));
        String finalStatesLine = br.readLine();
        assert (finalStatesLine.equals("Final states:"));
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) continue;
            turingMachine.makeStateFinal(line);
        }

        return turingMachine;
    }

    private void correctInputAlphabetLba() {
        if (inputAlphabet.contains(LBASTART)) inputAlphabet.remove(LBASTART);
        if (inputAlphabet.contains(LBAEND)) inputAlphabet.remove(LBAEND);
    }

    static TuringMachine parseFromJflapFile(BufferedReader br, String name) throws IOException {
        HashMap<String, TuringMachine> innerMachines = new HashMap<>();
        ArrayList<StateTuringMachine> blocks = new ArrayList<>();
        br.readLine();//"<!--The list of states.-->"
        
        StateTuringMachine block = readState(br);
        while (block != null) {
            blocks.add(block);
            block = readState(br);
        }

        ArrayList<TransitionTuringMachine> transitions = new ArrayList<>();
        TransitionTuringMachine transition = readTransitionJflap(br);
        while (transition != null) {
            transitions.add(transition);
            transition = readTransitionJflap(br);
        }

        String line;
        String closingTag = String.format("</%1$s>", name);

        while (true) {
            //"|| line.contains("<Machine")" is a quick but dirty hack. Because of it, other than my TM may not parse.
            while (((line = br.readLine().trim()).isEmpty() || line.contains("<Machine")) && !line.contains(closingTag));

            if (line.contains(closingTag)) return new TuringMachine(blocks, transitions, innerMachines);

            String innerMachineName = line.substring(line.indexOf('<') + 1, line.lastIndexOf('>'));
            TuringMachine innerTuringMachine = parseFromJflapFile(br, innerMachineName);
            innerMachines.put(innerMachineName, innerTuringMachine);
        }
    }

    private void addPrefixToAllStates(String prefix) {
        for (StateTuringMachine block : blocks) {
            block.name = prefix + block.name;
        }
    }

    static TuringMachine parseWholeJflapFileToTuringMachine(BufferedReader br) throws IOException {
        br.readLine();
        br.readLine();
        br.readLine();
        return parseFromJflapFile(br, "automaton");
    }

    private int findNextInnerTM(int index) {
        for (int i = index; i < blocks.size(); i++) {
            if (associatedMTs.containsKey(blocks.get(i).tag)) {
                return i;
            }
        }
        return -1;
    }

    void flatten() {
        int nextInnerTM = findNextInnerTM(0);
        int innerTMcount = 0;
        while (nextInnerTM != -1) {
            unrollTuringMachineFromBlock(blocks.get(nextInnerTM), innerTMcount++);
            nextInnerTM = findNextInnerTM(nextInnerTM);
        }
        associatedMTs.clear();
    }

    private int findMaximumBlockId() {
        return blocks.stream().map(x -> x.id).max(Comparator.naturalOrder()).get();
    }

    private void increaseAllStateIdsBy(int addThis) {
        for (StateTuringMachine state : blocks) {
            state.id += addThis;
        }
        for (TransitionTuringMachine trans : transitions) {
            trans.from += addThis;
            trans.to += addThis;
        }
    }

    private void unrollTuringMachineFromBlock(StateTuringMachine blockThatIsTM, int uniqueUnrollingId) {
        TuringMachine clonedTM = new TuringMachine(associatedMTs.get(blockThatIsTM.tag));
        clonedTM.increaseAllStateIdsBy(this.findMaximumBlockId() + 1);

        blocks.remove(blockThatIsTM);
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

    static void tapeRemoveEpsilons(List<Symbol> tape) {
        Symbol eps = Symbol.getSymbol(EPSILON);
        while (tape.contains(eps)) tape.remove(eps);
    }

    static boolean tapeContainsNonTerminal(List<Symbol> tape, HashSet<Symbol> terminals) {
        for (Symbol symbol : tape) {
            if (!terminals.contains(symbol)) return true;
        }
        return false;
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