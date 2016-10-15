package com.mtgrammars;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Alex on 02.10.2016.
 */
public class StateTuringMachine {
    final boolean isInitial;
    final boolean isFinal;
    final String tag;

    String name;
    int id;

    public StateTuringMachine(String name, int id, String tag, boolean isInitial, boolean isFinal) {
        this.name = name;
        this.id = id;
        this.tag = tag;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
    }

    public StateTuringMachine(StateTuringMachine block) {
        isInitial = block.isInitial;
        isFinal = block.isFinal;
        tag = block.tag;
        name = block.name;
        id = block.id;
    }

    static StateTuringMachine readState(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line.contains("<!--The list of transitions.-->")) return null;
            //it means that there's a transition, not a state, ahead.

        Pair<Integer, String> idNamePair = parseStateOpenTag(line);
        int id = idNamePair.fst;
        String name = idNamePair.snd;

        line = br.readLine();
        String machineName = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));

        boolean isInitial = false;
        boolean isFinal = false;
        for (int i = 0; i < 2; i++) {//we should check both for <initial> and <final>
            Pair<Boolean, Boolean> isInitialIsFinal = readUntilFinalOrInitialOrBlockEnd(br);
            if (isInitialIsFinal == null) break;
            isInitial = isInitial || isInitialIsFinal.fst;
            isFinal = isFinal || isInitialIsFinal.snd;
        }
        return new StateTuringMachine(name, id, machineName, isInitial, isFinal);
    }

    private static Pair<Boolean, Boolean> readUntilFinalOrInitialOrBlockEnd(BufferedReader br) throws IOException{
        String line;
        boolean isInitial = false;
        boolean isFinal = false;
        while ((line = br.readLine()) != null && !line.contains("<initial/>")
                && !line.contains("</block>") && !line.contains("<final/>"));

        if (line == null) return null;//means that end of the block was encountered.
        if (line.contains("<initial/>")) isInitial = true;
        if (line.contains("<final/>")) isFinal = true;
        if (!(isFinal || isInitial) && line.contains("</block>")) return null;
        return new Pair<>(isInitial, isFinal);
    }

    private static Pair<Integer, String> parseStateOpenTag(String stateOpenTagLine) {//something like '<block id="2" name="q2">'
        String[] tagItems = stateOpenTagLine.trim().split(" ");
        assert tagItems[0].contains("block");

        String idInQuotes = tagItems[1].split("=")[1];
        int id = Integer.parseInt(idInQuotes.substring(1, idInQuotes.length() - 1));

        String nameInQuotes = tagItems[2].split("=")[1];
        String name = nameInQuotes.substring(nameInQuotes.indexOf("\"") + 1, nameInQuotes.lastIndexOf("\""));
        return new Pair<>(id, name);
    }

    @Override
    public String toString() {
        return "StateTuringMachine{" +
                "isInitial=" + isInitial +
                ", isFinal=" + isFinal +
                ", tag='" + tag + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}