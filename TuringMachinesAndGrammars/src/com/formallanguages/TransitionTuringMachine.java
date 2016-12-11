package com.formallanguages;

import java.io.BufferedReader;
import java.io.IOException;

import static com.formallanguages.ParsingXMLUtilities.getXmlElementsContentSingleLine;

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

    static TransitionTuringMachine readTransitionJflap(BufferedReader br) throws IOException {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransitionTuringMachine that = (TransitionTuringMachine) o;

        if (from != that.from) return false;
        if (to != that.to) return false;
        if (read != null ? !read.equals(that.read) : that.read != null) return false;
        if (write != null ? !write.equals(that.write) : that.write != null) return false;
        return direction == that.direction;

    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        result = 31 * result + (read != null ? read.hashCode() : 0);
        result = 31 * result + (write != null ? write.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        return result;
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
