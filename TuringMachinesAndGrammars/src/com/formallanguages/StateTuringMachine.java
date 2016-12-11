package com.formallanguages;

import java.io.BufferedReader;
import java.io.IOException;

import static com.formallanguages.ParsingXMLUtilities.parseStateOpenTag;

public class StateTuringMachine {
    boolean isInitial;
    boolean isFinal;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateTuringMachine that = (StateTuringMachine) o;

        if (isInitial != that.isInitial) return false;
        if (isFinal != that.isFinal) return false;
        if (id != that.id) return false;
        if (tag != null ? !tag.equals(that.tag) : that.tag != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = (isInitial ? 1 : 0);
        result = 31 * result + (isFinal ? 1 : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + id;
        return result;
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