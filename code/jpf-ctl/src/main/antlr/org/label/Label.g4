grammar Label;

@header {
package org.label;
}

/* Parser Rules */

label
	: 'Initial'																	#Initial
	| 'End'																		#End
	| 'BooleanStaticField' 			QUALIFIEDNAME								#BooleanStaticField
	| 'IntegerStaticField' 			QUALIFIEDNAME								#IntegerStaticField
	| 'BooleanLocalVariable' 		QUALIFIEDNAME PARAMETERS ':' IDENTIFIER		#BooleanLocalVariable
	| 'IntegerLocalVariable' 		QUALIFIEDNAME PARAMETERS ':' IDENTIFIER 	#IntegerLocalVariable
	| 'InvokedMethod' 				QUALIFIEDNAME PARAMETERS 					#InvokedMethod
	| 'ReturnedVoidMethod' 			QUALIFIEDNAME PARAMETERS 					#ReturnedVoidMethod
	| 'ReturnedBooleanMethod' 		QUALIFIEDNAME PARAMETERS  					#ReturnedBooleanMethod
	| 'ReturnedIntegerMethod' 		QUALIFIEDNAME PARAMETERS  					#ReturnedIntegerMethod
	| 'ThrownException' 			QUALIFIEDNAME 								#ThrownException
	| 'SynchronizedStaticMethod'	QUALIFIEDNAME PARAMETERS  					#SynchronizedStaticMethod
	;

/* Lexer Rules */

// Whitespace and comments

WS:                 [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT:            '/*' .*? '*/'    -> channel(HIDDEN);
LINE_COMMENT:       '//' ~[\r\n]*    -> channel(HIDDEN);

// Identifiers

IDENTIFIER:         Letter LetterOrDigit*;

PARAMETERS
	: '(' FORMALPARAMETERLIST? ')'
	;

QUALIFIEDNAME
    : IDENTIFIER ('.' IDENTIFIER)*
    ;


// Fragment rules

fragment FORMALPARAMETERLIST
    : FORMALPARAMETER (',' FORMALPARAMETER)*
    ;

fragment FORMALPARAMETER
    : QUALIFIEDNAME ('[' ']')*
    ;

fragment LetterOrDigit
    : Letter
    | [0-9]
    ;

fragment Letter
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;