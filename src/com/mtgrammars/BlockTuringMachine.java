package com.mtgrammars;

import java.io.BufferedReader;

/**
 * Created by Alex on 02.10.2016.
 */
public class BlockTuringMachine {
    final boolean isInitial;
    final boolean isFinal;
    final String tag;
    String name;
    int id;
//    ArrayList<TransitionTuringMachine> transitions;

    public BlockTuringMachine(BlockTuringMachine block) {
        isInitial = block.isInitial;
        isFinal = block.isFinal;
        tag = block.tag;
        name = block.name;
        id = block.id;
    }

    static BlockTuringMachine readBlock(BufferedReader br) throws Exception {
        String line = br.readLine();
        if (line.contains("<!--The list of transitions.-->")) return null;

        String[] startTag = line.trim().split(" ");
        String idInQuotes = startTag[1].split("=")[1];
        int id = Integer.parseInt(idInQuotes.substring(1, idInQuotes.length() - 1));

        line = startTag[2].split("=")[1];
        String name = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));

        line = br.readLine();
        String machineName = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));

        while ((line = br.readLine()) != null && !line.contains("<initial/>") && !line.contains("</block>") && !line.contains("<final/>"));
        boolean isInitial = false;
        boolean isFinal = false;
        if (line != null) {
            if (line.contains("<initial/>")) isInitial = true;
            if (line.contains("<final/>")) isFinal = true;
        }
        while ((isInitial || isFinal) && (line = br.readLine()) != null && !line.contains("</block>"));
        return new BlockTuringMachine(name, id, machineName, isInitial, isFinal);
    }

    public BlockTuringMachine(String name, int id, String tag, boolean isInitial, boolean isFinal) {
        this.name = name;
        this.id = id;
        this.tag = tag;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
//        this.transitions = transitions;
    }

    @Override
    public String toString() {
        return "BlockTuringMachine{" +
                "isInitial=" + isInitial +
                ", tag='" + tag + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}