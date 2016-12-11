package com.formallanguages;

import javax.swing.plaf.synth.SynthButtonUI;
import java.util.*;
import java.util.stream.Collectors;
import com.formallanguages.TransitionTuringMachine.Direction;

import static com.formallanguages.DoubleSymbol.getDoubleSymbol;
import static com.formallanguages.DoubleSymbol.getRepeatedSymbol;
import static com.formallanguages.SpecialTuringMachineSymbols.*;
import static com.formallanguages.TransitionTuringMachine.Direction.Left;
import static com.formallanguages.TransitionTuringMachine.Direction.Right;

public class TuringMachineToGrammarConvertor {

    /** Converts Turing Machine to type zero grammar.
     * Implements algorithm, described in Martynenko's "Languages and translations". (ISBN 5-288-02870-2)
     */
    public static Grammar toType0Grammar(TuringMachine tm){
        HashSet<Symbol> terminals = tm.inputAlphabet.stream()
                .map(Symbol::getSymbol).collect(Collectors.toCollection(HashSet::new));
        HashSet<Symbol> nonterminals = new HashSet<>();
        for (String tapeSymbol : tm.tapeAlphabet) {
            for (String inputSymbol : tm.inputAlphabet) {
                nonterminals.add(DoubleSymbol.getDoubleSymbol(tapeSymbol, inputSymbol));
            }
            nonterminals.add(DoubleSymbol.getDoubleSymbol(EPSILON, tapeSymbol));
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

        nonterminals.addAll(tm.blocks.stream().map(x -> Symbol.getSymbol(x.name)).collect(Collectors.toList()));

        ArrayList<Production> productions = new ArrayList<>();
        productions.add(new Production(a1, Symbol.getSymbol(tm.initialState.name)));

        for (Symbol terminal : terminals) {
            productions.add(new Production(a2, Arrays.asList(getRepeatedSymbol(terminal), a2)));
        }
        productions.add(new Production(a2, a3));
        productions.add(new Production(a3, Arrays.asList(DoubleSymbol.getDoubleSymbol(EPSILON, BLANK), a3)));
        productions.add(new Production(a3, epsilon));

        for (TransitionTuringMachine transition : tm.transitions) {
            StateTuringMachine currentState = tm.blocks.stream().filter(x -> x.id == transition.from).findFirst().get();
            StateTuringMachine nextState = tm.blocks.stream().filter(x -> x.id == transition.to).findFirst().get();
            if (transition.direction == TransitionTuringMachine.Direction.Right) {
                for (Symbol a : terminalsAndEps) {
                    productions.add(new Production(
                             Arrays.asList(Symbol.getSymbol(currentState.name), getDoubleSymbol(a, transition.read)),
                             Arrays.asList(getDoubleSymbol(a, transition.write), Symbol.getSymbol(nextState.name)))
                    );
                }
                continue;
            }
            for (Symbol a : terminalsAndEps) {
                for (Symbol b :  terminalsAndEps) {
                    for (String E : tm.tapeAlphabet) {
                        DoubleSymbol bEsymbol = getDoubleSymbol(b, E);
                        productions.add(new Production(
                            Arrays.asList(bEsymbol, Symbol.getSymbol(currentState.name),
                                    getDoubleSymbol(a, transition.read)),
                            Arrays.asList(Symbol.getSymbol(nextState.name), bEsymbol,
                                    getDoubleSymbol(a, transition.write))));
                    }
                }
            }
        }

        for (StateTuringMachine block : tm.finalStates) {
            Symbol curBlockSymbol = Symbol.getSymbol(block.name);
            for (Symbol a : terminalsAndEps) {
                for (String C : tm.tapeAlphabet) {
                    DoubleSymbol aCsymbol = getDoubleSymbol(a, C);
                    productions.add(new Production(
                             Arrays.asList(aCsymbol, curBlockSymbol),
                             Arrays.asList(curBlockSymbol, a, curBlockSymbol)
                    ));
                    productions.add(new Production(
                             Arrays.asList(curBlockSymbol, aCsymbol),
                             Arrays.asList(curBlockSymbol, a, curBlockSymbol)
                    ));
                }
            }
            productions.add(new Production(curBlockSymbol, epsilon));
        }
        return new Grammar(nonterminals, terminals, a1, productions);
    }

    public static Grammar toType1Grammar(TuringMachine tm) {
        //TODO: make sure that tm.inputAlphabet contains proper values! Also, it shouldn't contain 'c' or 's'.
        HashSet<Symbol> terminals = tm.inputAlphabet.stream()
                .map(Symbol::getSymbol).collect(Collectors.toCollection(HashSet::new));
        HashSet<Symbol> nonterminals = new HashSet<>();

        Symbol a1 = Symbol.getSymbol("A1");
        Symbol a2 = Symbol.getSymbol("A2");
        nonterminals.add(a1);
        nonterminals.add(a2);

        Symbol start = Symbol.getSymbol(LBASTART);
        Symbol end = Symbol.getSymbol(LBAEND);

        ArrayList<Production> productions = new ArrayList<>();
        //TODO: decide to use c,s or LBASTART, LBAEND
        HashSet<String> tapeAlphabetNoMarkers = new HashSet<>(tm.tapeAlphabet
                .stream().filter(x -> !(x.equals("c") || x.equals("s"))).collect(Collectors.toList()));
        //(1)
        Symbol initialState = Symbol.getSymbol(tm.initialState.name);
        for (String a : tm.inputAlphabet) {
            Symbol aSymb = Symbol.getSymbol(a);
            ComplexSymbol newComplexSymbol = ComplexSymbol.get(Arrays.asList(initialState, start, aSymb, aSymb, end));
            //nonterminals.add(newComplexSymbol);
            productions.add(new Production(a1, newComplexSymbol));
        }
        //(2.*), (5.*), (6.*), (7.*)
        for (TransitionTuringMachine transition : tm.transitions) {
            StateTuringMachine currentState = tm.blocks.stream().filter(x -> x.id == transition.from).findFirst().get();
            StateTuringMachine nextState = tm.blocks.stream().filter(x -> x.id == transition.to).findFirst().get();
            Symbol q = Symbol.getSymbol(currentState.name);
            Symbol p = Symbol.getSymbol(nextState.name);
            if (transition.read.equals(LBASTART) && transition.write.equals(LBASTART)
                    && transition.direction.equals(TransitionTuringMachine.Direction.Right) && !currentState.isFinal) {
                //TODO: DO CHECK IF ALPHABETS ARE OK!
                for (String a : tm.inputAlphabet) {
                    for (String X : tapeAlphabetNoMarkers) {
                        //(2.1)
                        Symbol xSymb = Symbol.getSymbol(X);
                        Symbol aSymb = Symbol.getSymbol(a);
//                        ComplexSymbol left2 = ComplexSymbol.get(Arrays.asList(q, start, xSymb, aSymb, end));
//                        ComplexSymbol right2 = ComplexSymbol.get(Arrays.asList(start, p, xSymb, aSymb, end));
//                        productions.add(new Production(left2, right2));
                        //(5.1)
                        ComplexSymbol left5 = ComplexSymbol.get(Arrays.asList(q, start, xSymb, aSymb));
                        ComplexSymbol right5 = ComplexSymbol.get(Arrays.asList(start, p, xSymb, aSymb));
                        productions.add(new Production(left5, right5));
                    }
                }
                continue;
            }
            if (transitionReadWriteAreNotMarkers(transition) && transition.direction.equals(Left) && !currentState.isFinal) {
                Symbol X = Symbol.getSymbol(transition.read);
                Symbol Y = Symbol.getSymbol(transition.write);
                for (String a : tm.inputAlphabet) {
                    Symbol aSymb = Symbol.getSymbol(a);
                    //(2.2)
//                    ComplexSymbol left2 = ComplexSymbol.get(Arrays.asList(start, q, X, aSymb, end));
//                    ComplexSymbol right2 = ComplexSymbol.get(Arrays.asList(p, start, Y, aSymb, end));
//                    productions.add(new Production(left2, right2));
                    //(5.2)
                    ComplexSymbol left5 = ComplexSymbol.get(Arrays.asList(start, q, X, aSymb));
                    ComplexSymbol right5 = ComplexSymbol.get(Arrays.asList(p, start, Y, aSymb));
                    productions.add(new Production(left5, right5));

                    for (String z : tapeAlphabetNoMarkers) {
                        for (String b : tm.inputAlphabet) {
                            Symbol bSymb = Symbol.getSymbol(b);
                            Symbol Z = Symbol.getSymbol(z);
                            //(6.2)
                            List<Symbol> left62 = Arrays.asList(ComplexSymbol.get(Arrays.asList(Z, bSymb)), ComplexSymbol.get(Arrays.asList(q, X, aSymb)));
                            List<Symbol> right62 = Arrays.asList(ComplexSymbol.get(Arrays.asList(p, Z, bSymb)), ComplexSymbol.get(Arrays.asList(Y, aSymb)));
                            productions.add(new Production(left62, right62));
                            //(6.4)
                            List<Symbol> left64 = Arrays.asList(ComplexSymbol.get(Arrays.asList(start, Z, bSymb)), ComplexSymbol.get(Arrays.asList(q, X, aSymb)));
                            List<Symbol> right64 = Arrays.asList(ComplexSymbol.get(Arrays.asList(start, p, Z, bSymb)), ComplexSymbol.get(Arrays.asList(Y, aSymb)));
                            productions.add(new Production(left64, right64));
                            //(7.3)
                            List<Symbol> left7 = Arrays.asList(ComplexSymbol.get(Arrays.asList(Z, bSymb)), ComplexSymbol.get(Arrays.asList(q, X, aSymb, end)));
                            List<Symbol> right7 = Arrays.asList(ComplexSymbol.get(Arrays.asList(p, Z, bSymb)), ComplexSymbol.get(Arrays.asList(Y, aSymb, end)));
                            productions.add(new Production(left7, right7));
                        }
                    }
                }
                continue;
            }
            if (transitionReadWriteAreNotMarkers(transition) && transition.direction.equals(Right) && !currentState.isFinal) {
                Symbol X = Symbol.getSymbol(transition.read);
                Symbol Y = Symbol.getSymbol(transition.write);
                for (String a : tm.inputAlphabet) {
                    Symbol aSymb = Symbol.getSymbol(a);
                    //(2.3)
//                    ComplexSymbol left2 = ComplexSymbol.get(Arrays.asList(start, q, X, aSymb, end));
//                    ComplexSymbol right2 = ComplexSymbol.get(Arrays.asList(start, Y, aSymb, p, end));
//                    productions.add(new Production(left2, right2));
                    for (String z : tapeAlphabetNoMarkers) {
                        for (String b : tm.inputAlphabet) {
                            Symbol bSymb = Symbol.getSymbol(b);
                            Symbol Z =Symbol.getSymbol(z);
                            //(5.3)
                            List<Symbol> left5 = Arrays.asList(ComplexSymbol.get(Arrays.asList(start, q, X, aSymb)),
                                    ComplexSymbol.get(Arrays.asList(Z, bSymb)));
                            List<Symbol> right5 = Arrays.asList(ComplexSymbol.get(Arrays.asList(start, Y, aSymb)),
                                    ComplexSymbol.get(Arrays.asList(p, Z, bSymb)));
                            productions.add(new Production(left5, right5));
                            //(6.1)
                            List<Symbol> left61 = Arrays.asList(ComplexSymbol.get(Arrays.asList(q, X, aSymb)), ComplexSymbol.get(Arrays.asList(Z, bSymb)));
                            List<Symbol> right61 = Arrays.asList(ComplexSymbol.get(Arrays.asList(Y, aSymb)), ComplexSymbol.get(Arrays.asList(p, Z, bSymb)));
                            productions.add(new Production(left61, right61));
                            //(6.3)
                            List<Symbol> left63 = Arrays.asList(ComplexSymbol.get(Arrays.asList(q, X, aSymb)), ComplexSymbol.get(Arrays.asList(Z, bSymb, end)));
                            List<Symbol> right63 = Arrays.asList(ComplexSymbol.get(Arrays.asList(Y, aSymb)), ComplexSymbol.get(Arrays.asList(p, Z, bSymb, end)));
                            productions.add(new Production(left63, right63));
                            //(7.1)
                            ComplexSymbol left7 = ComplexSymbol.get(Arrays.asList(q, X, aSymb, end));
                            ComplexSymbol right7 = ComplexSymbol.get(Arrays.asList(Y, aSymb, p, end));
                            productions.add(new Production(left7, right7));
                        }
                    }
                }
                continue;
            }
            if (transition.read.equals(LBAEND) && transition.write.equals(LBAEND) && transition.direction.equals(Left) && !currentState.isFinal) {
                for (String a : tm.inputAlphabet) {
                    for (String x : tapeAlphabetNoMarkers) {
                        Symbol X = Symbol.getSymbol(x);
                        Symbol aSymb = Symbol.getSymbol(a);
                        //(2.4)
//                        ComplexSymbol left2 = ComplexSymbol.get(Arrays.asList(start, X, aSymb, q, end));
//                        ComplexSymbol right2 = ComplexSymbol.get(Arrays.asList(start, p, X, aSymb, end));
//                        productions.add(new Production(left2, right2));
                        //(7.2)
                        ComplexSymbol left7 = ComplexSymbol.get(Arrays.asList(X, aSymb, q, end));
                        ComplexSymbol right7 = ComplexSymbol.get(Arrays.asList(p, X, aSymb, end));
                        productions.add(new Production(left7, right7));
                    }
                }
            }
        }
        //(3.*), (8.*)
        for (StateTuringMachine finalState : tm.finalStates) {
            Symbol q = Symbol.getSymbol(finalState.name);
            for (String a : tm.inputAlphabet) {
                for (String x : tapeAlphabetNoMarkers) {
                    Symbol aSymb = Symbol.getSymbol(a);
                    Symbol X = Symbol.getSymbol(x);
                    //(3.*)
//                    ComplexSymbol left31 = ComplexSymbol.get(Arrays.asList(q, start, X, aSymb, end));
//                    ComplexSymbol left32 = ComplexSymbol.get(Arrays.asList(start, q, X, aSymb, end));
//                    ComplexSymbol left33 = ComplexSymbol.get(Arrays.asList(start, X, aSymb, q, end));
//                    productions.add(new Production(left31, aSymb));
//                    productions.add(new Production(left32, aSymb));
//                    productions.add(new Production(left33, aSymb));
                    //(8.*)
                    ComplexSymbol left81 = ComplexSymbol.get(Arrays.asList(q, start, X, aSymb));
                    ComplexSymbol left82 = ComplexSymbol.get(Arrays.asList(start, q, X, aSymb));
                    ComplexSymbol left83 = ComplexSymbol.get(Arrays.asList(q, X, aSymb));
                    ComplexSymbol left84 = ComplexSymbol.get(Arrays.asList(q, X, aSymb, end));
                    ComplexSymbol left85 = ComplexSymbol.get(Arrays.asList(X, aSymb, q, end));
                    productions.add(new Production(left81, aSymb));
                    productions.add(new Production(left82, aSymb));
                    productions.add(new Production(left83, aSymb));
                    productions.add(new Production(left84, aSymb));
                    productions.add(new Production(left85, aSymb));
                }
            }
        }
        //(4.*)
        for (String a : tm.inputAlphabet) {
            Symbol aSymb = Symbol.getSymbol(a);
            //(4.1)
            List<Symbol> right41 = Arrays.asList(ComplexSymbol.get(Arrays.asList(initialState, start, aSymb, aSymb)), a2);
            productions.add(new Production(a1, right41));
            //(4.2)
            List<Symbol> right42 = Arrays.asList(ComplexSymbol.get(Arrays.asList(aSymb, aSymb)), a2);
            productions.add(new Production(a2, right42));
            //(4.3)
            ComplexSymbol right43 = ComplexSymbol.get(Arrays.asList(aSymb, aSymb, end));
            productions.add(new Production(a2, right43));
        }
        //(9.*)
        for (String a : tm.inputAlphabet) {
            Symbol aSymb = Symbol.getSymbol(a);
            for (String b : tm.inputAlphabet) {
                Symbol bSymb = Symbol.getSymbol(b);
                for (String x : tapeAlphabetNoMarkers) {
                    Symbol X = Symbol.getSymbol(x);
                    List<Symbol> left91 = Arrays.asList(aSymb, ComplexSymbol.get(Arrays.asList(X, bSymb)));
                    List<Symbol> left92 = Arrays.asList(aSymb, ComplexSymbol.get(Arrays.asList(X, bSymb, end)));
                    List<Symbol> left93 = Arrays.asList(ComplexSymbol.get(Arrays.asList(X, aSymb)), bSymb);
                    List<Symbol> left94 = Arrays.asList(ComplexSymbol.get(Arrays.asList(start, X, aSymb)), bSymb);
                    List<Symbol> right9 = Arrays.asList(aSymb, bSymb);
                    productions.add(new Production(left91, right9));
                    productions.add(new Production(left92, right9));
                    productions.add(new Production(left93, right9));
                    productions.add(new Production(left94, right9));
                }
            }
        }
        //nonterminals
        for (String A : tm.inputAlphabet) {
            Symbol a = Symbol.getSymbol(A);
            for (String x : tapeAlphabetNoMarkers) {
                Symbol X = Symbol.getSymbol(x);
                for (StateTuringMachine state : tm.blocks) {
                    Symbol q = Symbol.getSymbol(state.name);
                    List<ComplexSymbol> s = new LinkedList<>();
                    s.add(ComplexSymbol.get(Arrays.asList(q, start, X, a, end)));
                    s.add(ComplexSymbol.get(Arrays.asList(start, q, X, a, end)));
                    s.add(ComplexSymbol.get(Arrays.asList(start, X, a, q, end)));
                    s.add(ComplexSymbol.get(Arrays.asList(q, X, a)));
                    s.add(ComplexSymbol.get(Arrays.asList(q, X, a, end)));
                    s.add(ComplexSymbol.get(Arrays.asList(X, a, q, end)));
                    s.add(ComplexSymbol.get(Arrays.asList(start, X, a)));
                    s.add(ComplexSymbol.get(Arrays.asList(X, a)));
                    s.add(ComplexSymbol.get(Arrays.asList(X, a, end)));
//                    s.set(0, ComplexSymbol.get(Arrays.asList(q, start, X, a, end)));
//                    s.set(1, ComplexSymbol.get(Arrays.asList(start, q, X, a, end)));
//                    s.set(2, ComplexSymbol.get(Arrays.asList(start, X, a, q, end)));
//                    s.set(3, ComplexSymbol.get(Arrays.asList(q, X, a)));
//                    s.set(4, ComplexSymbol.get(Arrays.asList(q, X, a, end)));
//                    s.set(5, ComplexSymbol.get(Arrays.asList(X, a, q, end)));
//                    s.set(6, ComplexSymbol.get(Arrays.asList(start, X, a)));
//                    s.set(7, ComplexSymbol.get(Arrays.asList(X, a)));
//                    s.set(8, ComplexSymbol.get(Arrays.asList(X, a, end)));
                    nonterminals.addAll(s);
                }
            }
        }
        return new Grammar(nonterminals, terminals, a1, productions);
    }

    private static boolean transitionReadWriteAreNotMarkers(TransitionTuringMachine transition) {
        return !(transition.read.equals(LBASTART) || transition.read.equals(LBAEND) || transition.write.equals(LBAEND) || transition.write.equals(LBASTART));
    }
}