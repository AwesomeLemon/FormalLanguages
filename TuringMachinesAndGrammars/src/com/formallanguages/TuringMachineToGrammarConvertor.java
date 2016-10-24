package com.formallanguages;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.formallanguages.CompositeSymbol.getCompositeSymbol;
import static com.formallanguages.CompositeSymbol.getRepeatedSymbol;
import static com.formallanguages.SpecialTuringMachineSymbols.*;

/**
 * Created by Alex on 10.10.2016.
 */
public class TuringMachineToGrammarConvertor {
    public static Grammar TuringMachineToGrammar0(TuringMachine tm){
        HashSet<Symbol> terminals = tm.inputAlphabet.stream()
                .map(Symbol::getSymbol).collect(Collectors.toCollection(HashSet::new));
        HashSet<Symbol> nonterminals = new HashSet<>();
        for (String tapeSymbol : tm.tapeAlphabet) {
            for (String inputSymbol : tm.inputAlphabet) {
                nonterminals.add(getCompositeSymbol(tapeSymbol, inputSymbol));
            }
            nonterminals.add(getCompositeSymbol(EPSILON, tapeSymbol));
        }
        HashSet<Symbol> terminalsAndEps = new HashSet<>(terminals);
        Symbol epsilon = Symbol.getSymbol(EPSILON);
        terminalsAndEps.add(epsilon);//there's need to iterate over such series several times.

        Symbol a1 = Symbol.getSymbol("A1");
        Symbol a2 = Symbol.getSymbol("A2");
        Symbol a3 = Symbol.getSymbol("A3");
        nonterminals.add(a1);
        nonterminals.add(a2);
        nonterminals.add(a3);

        nonterminals.addAll(tm.blocks.stream().map(x -> Symbol.getSymbol(x.name))
                .collect(Collectors.toList()));

        ArrayList<Production> productions = new ArrayList<>();
        productions.add(new Production(a1, Symbol.getSymbol(tm.initialState.name)));

        for (Symbol terminal : terminals) {
            productions.add(new Production(a2, Arrays.asList(getRepeatedSymbol(terminal), a2)));
        }
        productions.add(new Production(a2, a3));
        productions.add(new Production(a3, Arrays.asList(getCompositeSymbol(EPSILON, BLANK), a3)));
        productions.add(new Production(a3, epsilon));

        for (TransitionTuringMachine transition : tm.transitions) {
            StateTuringMachine currentState = tm.blocks.stream().filter(x -> x.id == transition.from).findFirst().get();
            StateTuringMachine nextState = tm.blocks.stream().filter(x -> x.id == transition.to).findFirst().get();
            if (transition.direction == TransitionTuringMachine.Direction.Right) {
                for (Symbol a : terminalsAndEps) {
                    productions.add(new Production(
                            Stream.of(Symbol.getSymbol(currentState.name), getCompositeSymbol(a, transition.read))
                                    .collect(Collectors.toList()),
                            Stream.of(getCompositeSymbol(a, transition.write), Symbol.getSymbol(nextState.name))
                                    .collect(Collectors.toList()))
                    );
                }
            }
            else {
                for (Symbol a : terminalsAndEps) {
                    for (Symbol b :  terminalsAndEps) {
                        for (String E : tm.tapeAlphabet) {
                            CompositeSymbol bEsymbol = getCompositeSymbol(b, E);
                            productions.add(new Production(
                                    Stream.of(bEsymbol, Symbol.getSymbol(currentState.name), getCompositeSymbol(a, transition.read)).collect(Collectors.toList()),
                                    Stream.of(Symbol.getSymbol(nextState.name), bEsymbol, getCompositeSymbol(a, transition.write)).collect(Collectors.toList())
                            ));
                        }
                    }
                }
            }
        }

        for (StateTuringMachine block : tm.finalStates) {
            Symbol curBlockSymbol = Symbol.getSymbol(block.name);
            for (Symbol a : terminalsAndEps) {
                for (String C : tm.tapeAlphabet) {
                    CompositeSymbol aCsymbol = getCompositeSymbol(a, C);
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
        return new Grammar(nonterminals, terminals, a1, productions);
    }
}