grammar Label;

@header {
package org.label;
}

/* Parser Rules */

label
	: 'Initial'																		#Initial
	| 'End'																			#End
	| 'BooleanStaticField' 			referenceType									#BooleanStaticField
	| 'BooleanLocalVariable' 		referenceType parameters ':' variableType value	#BooleanLocalVariable
	| 'InvokedMethod' 				referenceType parameters 						#InvokedMethod
	| 'ReturnedVoidMethod' 			referenceType parameters 						#ReturnedVoidMethod
	| 'ReturnedBooleanMethod' 		referenceType parameters value 					#ReturnedBooleanMethod
	| 'ThrownException' 			referenceType 									#ThrownException
	| 'SynchronizedStaticMethod'	referenceType parameters  						#SynchronizedStaticMethod
	;


value
	: 'true'
	| 'false'
	;

// Types, values, variables

primitiveType
	:	numericType
	|	'boolean'
	;

numericType
	:	integralType
	|	floatingPointType 
	;

integralType
	:	'byte'
	|	'short'
	|	'int'
	|	'long'
	|	'char'
	;

floatingPointType
	:	'float'
	|	'double'
	;

referenceType
	:	classType
	|	variableType
	|	arrayType
	;

classType
	: 	fullyQualifiedName typeArguments? 
	| 	classType ('.' fullyQualifiedName typeArguments?)
	;

variableType
	: 	IDENTIFIER
	;

arrayType
	:	primitiveType DIMS
	|	classType DIMS
	|	variableType DIMS
	;

typeArguments
	:	'<' typeArgumentList '>'
	;

typeArgumentList
	:	typeArgument (',' typeArgument)*
	;

typeArgument
	: unannType 
	| '?'
	;

// Fully Qualified Names

fullyQualifiedName
	:	IDENTIFIER
	|	packageName '.' IDENTIFIER
	;

packageName
	:	IDENTIFIER
	|	packageName '.' IDENTIFIER
	;

// Types

unannType
	:	primitiveType
	|	referenceType
	;

// Parameters 

parameters
	:	'(' formalParameterList? ')'
	;

formalParameterList
	:	unannType (',' unannType)*
	;

/* Lexer Rules */

// Whitespace and comments

WS:                 [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT:            '/*' .*? '*/'    -> channel(HIDDEN);
LINE_COMMENT:       '//' ~[\r\n]*    -> channel(HIDDEN);

// Reserve Words Matcher

ABSTRACT : 'abstract';
ASSERT : 'assert';
BOOLEAN : 'boolean';
BREAK : 'break';
BYTE : 'byte';
CASE : 'case';
CATCH : 'catch';
CHAR : 'char';
CLASS : 'class';
CONST : 'const';
CONTINUE : 'continue';
DEFAULT : 'default';
DO : 'do';
DOUBLE : 'double';
ELSE : 'else';
ENUM : 'enum';
EXTENDS : 'extends';
FINAL : 'final';
FINALLY : 'finally';
FLOAT : 'float';
FOR : 'for';
IF : 'if';
GOTO : 'goto';
IMPLEMENTS : 'implements';
IMPORT : 'import';
INSTANCEOF : 'instanceof';
INT : 'int';
INTERFACE : 'interface';
LONG : 'long';
NATIVE : 'native';
NEW : 'new';
PACKAGE : 'package';
PRIVATE : 'private';
PROTECTED : 'protected';
PUBLIC : 'public';
RETURN : 'return';
SHORT : 'short';
STATIC : 'static';
STRICTFP : 'strictfp';
SUPER : 'super';
SWITCH : 'switch';
SYNCHRONIZED : 'synchronized';
THIS : 'this';
THROW : 'throw';
THROWS : 'throws';
TRANSIENT : 'transient';
TRY : 'try';
VOID : 'void';
VOLATILE : 'volatile';
WHILE : 'while';

// Identifiers

IDENTIFIER
	: IdentifierStart IdentifierPart*
	;

DIMS
	:	'[' ']' ('[' ']')*
	;

// Fragment rules

fragment IdentifierPart
    : IdentifierStart
    | [0-9]
    ;

fragment IdentifierStart
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;