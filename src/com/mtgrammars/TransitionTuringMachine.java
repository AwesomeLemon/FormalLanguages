package com.mtgrammars;

import java.io.BufferedReader;

import static com.mtgrammars.ParsingXMLUtilities.getXmlElementsContentSingleLine;

/**
 * Created by Alex on 02.10.2016.
 */
public class TransitionTuringMachine {
    int from;
    int to;
    String read;
    String write;
    Direction direction;

    public enum Direction {Left, Right}

    static TransitionTuringMachine readTransition(BufferedReader br) throws Exception {
        String begin = br.readLine();
        if (begin.contains("<!--The list of automata-->")) return null;

        String value = getXmlElementsContentSingleLine(br.readLine());
        int from = Integer.parseInt(value);

        value = getXmlElementsContentSingleLine(br.readLine());
        int to = Integer.parseInt(value);

        value = getXmlElementsContentSingleLine(br.readLine());
        String read = value == null ? "blank" : value;

        value = getXmlElementsContentSingleLine(br.readLine());
        String write =  value == null ? "blank" : value;

        value = getXmlElementsContentSingleLine(br.readLine());
        TransitionTuringMachine.Direction dir = value.equals("L") ? TransitionTuringMachine.Direction.Left : TransitionTuringMachine.Direction.Right;

        br.readLine();//</transition>

        return new TransitionTuringMachine(from, to, read, write, dir);
    }

    @Override
    public String toString() {
        return "TransitionTuringMachine{" +
                "from=" + from +
                ", to=" + to +
                ", read='" + read + '\'' +
                ", write='" + write + '\'' +
                ", direction=" + direction +
                '}';
    }

    public TransitionTuringMachine(int from, int to, String read, String write, Direction direction) {
        this.from = from;
        this.to = to;
        this.read = read;
        this.write = write;
        this.direction = direction;
    }
}
