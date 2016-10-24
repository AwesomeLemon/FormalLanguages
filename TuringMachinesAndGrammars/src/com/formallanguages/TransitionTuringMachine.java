package com.formallanguages;

import java.io.BufferedReader;
import java.io.IOException;

import static com.formallanguages.ParsingXMLUtilities.getXmlElementsContentSingleLine;

/**
 * Created by Alex on 02.10.2016.
 */
public class TransitionTuringMachine {
    int from;
    int to;
    final String read;
    final String write;
    final Direction direction;

    public enum Direction {Left, Right}

    public TransitionTuringMachine(TransitionTuringMachine transition) {
        this.from = transition.from;
        this.to = transition.to;
        this.read = transition.read;
        this.write = transition.write;
        this.direction = transition.direction;
    }

    public TransitionTuringMachine(int from, int to, String read, String write, Direction direction) {
        this.from = from;
        this.to = to;
        this.read = read;
        this.write = write;
        this.direction = direction;
    }

    static TransitionTuringMachine readTransition(BufferedReader br) throws IOException {
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
        TransitionTuringMachine.Direction dir = value.equals("L") ? TransitionTuringMachine.Direction.Left
                                                                  : TransitionTuringMachine.Direction.Right;

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
}
