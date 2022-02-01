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
 */

grammar CTL;

@header {
package org.ctl;
}

/* Formulas */

formula
    : '(' formula ')'            #Bracket
    | '!' formula                #Not
    | 'true'                #True
    | 'false'                #False
    | ATOMIC_PROPOSITION            #AtomicProposition
    | 'AX' formula                #ForAllNext
    | 'AG' formula                #ForAllAlways
    | 'AF' formula                #ForAllEventually
    | 'EX' formula                #ExistsNext
    | 'EG' formula                #ExistsAlways
    | 'EF' formula                #ExistsEventually
    | <assoc=right> formula 'AU'  formula    #ForAllUntil
    | <assoc=right> formula 'EU'  formula    #ExistsUntil
    | <assoc=left> formula '&&'  formula     #And
    | <assoc=left> formula '||'  formula    #Or
    | <assoc=right> formula '->'  formula    #Implies
    | <assoc=left> formula '<->'  formula    #Iff
    ;

/* Atomic propositions */

ATOMIC_PROPOSITION
    : SIMPLE_TYPE ('.' SIMPLE_TYPE)*
    ;

SIMPLE_TYPE
	:	ALPHA (ALPHA | DIGIT)*
	;

DIGIT: [0-9];
ALPHA: [a-zA-Z_];

/* Skip white space */

WS : [ \t\r\n\u000C]+ -> skip ; 