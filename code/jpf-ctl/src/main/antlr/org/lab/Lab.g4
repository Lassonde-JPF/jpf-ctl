grammar Lab;

@header {
package org.label;
}

/* Parser Rules */

index : STATE+ '="' VAL '"' (' ' STATE+ '="' VAL '"')* ;

entry : STATE ": " STATE ;

/* Lexer Rules */

VAL : [a-zA-Z0-9$_]+ ;

STATE : '-'? digit+ ;

// Fragments

fragment digit : [0-9] ;