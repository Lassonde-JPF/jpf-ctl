grammar Trs;

@header {
package org.trs;
}

/* Parser Rules */

transition : STATE '->' STATE ;

partial : STATE? (' ' STATE)*

/* Lexer Rules */

STATE : '-'? digit+ ;

// Fragments

fragment digit : [0-9] ;