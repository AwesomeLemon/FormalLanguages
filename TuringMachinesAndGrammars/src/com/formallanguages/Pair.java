package com.formallanguages;

/**
 * Created by Alex on 15.10.2016.
 */
class Pair<T1, T2> {
    final T1 fst;
    final T2 snd;

    Pair(T1 fst, T2 snd) {
        this.fst = fst;
        this.snd = snd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (!fst.equals(pair.fst)) return false;
        return snd.equals(pair.snd);

    }

    @Override
    public int hashCode() {
        int result = fst.hashCode();
        result = 31 * result + snd.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" + fst +
                ", " + snd +
                '}';
    }
}