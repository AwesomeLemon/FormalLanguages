package com.formallanguages;

/**
 * Created by Alex on 15.10.2016.
 */
class Pair<T1, T2> {
    T1 fst;
    T2 snd;

    public Pair(T1 fst, T2 snd) {
        this.fst = fst;
        this.snd = snd;
    }
}