package com.formallanguages;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Alex on 15.10.2016.
 */
class GrammarUtilities {
    private GrammarUtilities() {}

    static String tapeToString(List<Symbol> tape) {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : tape) {
            if (!symbol.value.equals("eps")) sb.append(symbol.value);
        }
        return sb.toString();
    }

    static String otherTapeToString(List<Symbol> tape) {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : tape) {
            if (symbol.value.contains("q")) sb.append("(").append(symbol.value).append(")");
            if (symbol instanceof CompositeSymbol) {
                String sym = ((CompositeSymbol) symbol).value2;
                if (sym.equals("blank")) {
                    sb.append("_");
                    continue;
                }
                if (!sym.equals("eps")) sb.append(sym);
            }
        }
        return sb.toString();
    }

    static void tapeRemoveEpsilons(List<Symbol> tape) {
        Symbol eps = new Symbol("eps");
        while (tape.contains(eps)) tape.remove(eps);
    }

    static boolean tapeContainsNonTerminal(List<Symbol> tape, HashSet<Symbol> terminals) {
        for (Symbol symbol : tape) {
            if (!terminals.contains(symbol)) return true;
        }
        return false;
    }
}
