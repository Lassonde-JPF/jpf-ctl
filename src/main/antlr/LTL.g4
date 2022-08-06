/*
 * Copyright (C)  2021
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 *
 * @author Jinho Hwang
 * @author Minh Cu (Huy Cu)
 * @author Alberto Mastrofrancesco
 * @author Qasim Ahmed
 * @author Matt Walker
 * @author Zainab Fatmi
 */

grammar LTL;

@header {
package jpf.logic.ltl;
}

/* Formulas */

formula
    : '(' formula ')'                       #Bracket
    | '!' formula                           #Not
    | 'true'                                #True
    | 'false'                               #False
    | ALIAS                                 #Alias
    | 'X' formula                           #Next
    | 'G' formula                           #Always
    | 'F' formula                           #Eventually
    | <assoc=right> formula 'U' formula     #Until
    | <assoc=left> formula '&&' formula     #And
    | <assoc=left> formula '||' formula     #Or
    | <assoc=right> formula '->' formula    #Implies
    | <assoc=left> formula '<->' formula    #Iff
    ;

/* Atomic propositions */

ALIAS
    : SIMPLE_TYPE ('.' SIMPLE_TYPE)*
    ;

SIMPLE_TYPE
    : ALPHA (ALPHA | DIGIT)*
    ;

DIGIT: [0-9];
ALPHA: [a-zA-Z_];

/* Skip white space */

WS : [ \t\r\n\u000C]+ -> skip;
