package com.mtgrammars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alex on 10.10.2016.
 */
public class TuringMachineConvertor {
    public static Grammar TuringMachineToGrammar0(TuringMachine tm){
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


        Symbol a1 = new Symbol("A1");
        Symbol a2 = new Symbol("A2");
        Symbol a3 = new Symbol("A3");
        nonterminals.add(a1);
        nonterminals.add(a2);
        nonterminals.add(a3);

        nonterminals.addAll(tm.blocks.stream().map(x -> new Symbol(x.name))
                .collect(Collectors.toList()));

        ArrayList<Production> productions = new ArrayList<>();
        productions.add(new Production(a1, new Symbol(tm.initialState.name), a2));
        for (Symbol terminal : terminals) {
            productions.add(new Production(a2, new CompositeSymbol(terminal), a2));
        }
        productions.add(new Production(a2, a3));
        productions.add(new Production(a3, new CompositeSymbol("eps", "blank"), a3));
        productions.add(new Production(a3, epsilon));
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
                            Stream.of(new Symbol(currentBlock.name), new CompositeSymbol(a, transition.read))
                                    .collect(Collectors.toList()),
                            Stream.of(new CompositeSymbol(a, transition.write), new Symbol(nextBlock.name))
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
                                    Stream.of(bEsymbol, new Symbol(currentBlock.name), new CompositeSymbol(a, transition.read)).collect(Collectors.toList()),
                                    Stream.of(new Symbol(nextBlock.name), bEsymbol, new CompositeSymbol(a, transition.write)).collect(Collectors.toList())
                            ));
                        }
                    }
                }
            }

        }
        for (BlockTuringMachine block : tm.finalStates) {
            for (Symbol a : terminalsAndEps) {
                for (String C : tm.tapeAlphabet) {
                    CompositeSymbol aCsymbol = new CompositeSymbol(a, C);
                    Symbol curBlockSymbol = new Symbol(block.name);
                    productions.add(new Production(
                            Stream.of(aCsymbol, curBlockSymbol).collect(Collectors.toList()),
                            Stream.of(curBlockSymbol, a, curBlockSymbol).collect(Collectors.toList())
                    ));
                    productions.add(new Production(
                            Stream.of(curBlockSymbol, aCsymbol).collect(Collectors.toList()),
                            Stream.of(curBlockSymbol, a, curBlockSymbol).collect(Collectors.toList())
                    ));
                    productions.add(new Production(curBlockSymbol, epsilon));
                }
            }
        }
        return new Grammar(nonterminals, terminals, a1, productions);
    }
}
