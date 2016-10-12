package com.mtgrammars;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alex on 10.10.2016.
 */
public class TuringMachineConvertor {
    public static Grammar TuringMachineToGrammar0(TuringMachine tm){
        return TuringMachineToGrammar0(tm, "");
    }
    public static Grammar TuringMachineToGrammar0(TuringMachine tm, String prefix){
        HashSet<Symbol> terminals = tm.inputAlphabet.stream()
                .map(Symbol::new).collect(Collectors.toCollection(HashSet::new));
        HashSet<Symbol> nonterminals = new HashSet<>();
        for (String tapeSymbol : tm.tapeAlphabet) {
            for (String inputSymbol : tm.inputAlphabet) {
                nonterminals.add(new CompositeSymbol(tapeSymbol, inputSymbol));
            }
            nonterminals.add(new CompositeSymbol("eps", tapeSymbol));
        }
        HashSet<Symbol> terminalsAndEps = new HashSet<>(terminals);
        Symbol epsilon = new Symbol("eps");
        terminalsAndEps.add(epsilon);


        Symbol a1 = new Symbol(prefix + "A1");
        Symbol a2 = new Symbol(prefix + "A2");
        Symbol a3 = new Symbol(prefix + "A3");
        nonterminals.add(a1);
        nonterminals.add(a2);
        nonterminals.add(a3);

        nonterminals.addAll(tm.blocks.stream().map(x -> new Symbol(prefix + x.name))
                .collect(Collectors.toList()));

        ArrayList<Production> productions = new ArrayList<>();
        if (prefix.isEmpty()) productions.add(new Production(a1, new Symbol(prefix + tm.initialState.name), a2));
        else productions.add(new Production(a1, new Symbol(prefix + tm.initialState.name)));

        if (prefix.isEmpty()) {
            for (Symbol terminal : terminals) {
                productions.add(new Production(a2, new CompositeSymbol(terminal), a2));
            }
            productions.add(new Production(a2, a3));
            productions.add(new Production(a3, new CompositeSymbol("eps", "blank"), a3));
            productions.add(new Production(a3, epsilon));
        }
        for (TransitionTuringMachine transition : tm.transitions) {
            //TODO: DANGER! IT'S NOT NECCESSARY TRUE THAT BLOCK AT INDEX 'I' HAS ID == 'I'! OR IS IT?
//            if (transition.from >= tm.blocks.size() || transition.to >= tm.blocks.size()) {
//                int a000 = 1;
//            }
//            BlockTuringMachine currentBlock = tm.blocks.get(transition.from);
//            BlockTuringMachine nextBlock = tm.blocks.get(transition.to);
            BlockTuringMachine currentBlock = tm.blocks.stream().filter(x -> x.id == transition.from).findFirst().get();
            BlockTuringMachine nextBlock = tm.blocks.stream().filter(x -> x.id == transition.to).findFirst().get();
            if (transition.direction == TransitionTuringMachine.Direction.Right) {
                for (Symbol a : terminalsAndEps) {
                    productions.add(new Production(
                            Stream.of(new Symbol(prefix + currentBlock.name), new CompositeSymbol(a, transition.read))
                                    .collect(Collectors.toList()),
                            Stream.of(new CompositeSymbol(a, transition.write), new Symbol(prefix + nextBlock.name))
                                    .collect(Collectors.toList()))
                    );
                }
            }
            else {
                for (Symbol a : terminalsAndEps) {
                    for (Symbol b :  terminalsAndEps) {
                        for (String E : tm.tapeAlphabet) {
                            CompositeSymbol bEsymbol = new CompositeSymbol(b, E);
                            productions.add(new Production(
                                    Stream.of(bEsymbol, new Symbol(prefix + currentBlock.name), new CompositeSymbol(a, transition.read)).collect(Collectors.toList()),
                                    Stream.of(new Symbol(prefix + nextBlock.name), bEsymbol, new CompositeSymbol(a, transition.write)).collect(Collectors.toList())
                            ));
                        }
                    }
                }
            }

        }
        if (prefix.isEmpty()) {
            for (BlockTuringMachine block : tm.finalStates) {
                Symbol curBlockSymbol = new Symbol(block.name);
                for (Symbol a : terminalsAndEps) {
                    for (String C : tm.tapeAlphabet) {
                        CompositeSymbol aCsymbol = new CompositeSymbol(a, C);
                        productions.add(new Production(
                                Stream.of(aCsymbol, curBlockSymbol).collect(Collectors.toList()),
                                Stream.of(curBlockSymbol, a, curBlockSymbol).collect(Collectors.toList())
                        ));
                        productions.add(new Production(
                                Stream.of(curBlockSymbol, aCsymbol).collect(Collectors.toList()),
                                Stream.of(curBlockSymbol, a, curBlockSymbol).collect(Collectors.toList())
                        ));
                    }
                }
                productions.add(new Production(curBlockSymbol, epsilon));
            }
        }
        Grammar res = new Grammar(nonterminals, terminals, a1, productions);
        for (Map.Entry<String, TuringMachine> tmInnerEntry : tm.associatedMTs.entrySet()) {
            String tmInnerNameWithDot = tmInnerEntry.getKey();
            String tmInnerName = tmInnerNameWithDot.substring(0, tmInnerNameWithDot.lastIndexOf('.'));
            TuringMachine tmInner = tmInnerEntry.getValue();
            Grammar grammarInner = TuringMachineToGrammar0(tmInner, tmInnerName);
            res.mergeWithGrammarAndRename(grammarInner, tmInnerName, tmInner);
        }
        return res;
    }
}
