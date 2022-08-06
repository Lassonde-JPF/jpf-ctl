/*
 * Copyright (C)  2022
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
 * @author Matt Walker
 */

grammar Label;

@header {
package jpf.logic;
}

/* Label */

label
     : 'Initial'                                                                   #Initial
     | 'End'	                                                                   #End
     | 'BooleanStaticField' fieldName booleanValue                                 #BooleanStaticField
     | 'BooleanLocalVariable' methodName parameters ':' variableName booleanValue  #BooleanLocalVariable
     | 'InvokedMethod' methodName parameters                                       #InvokedMethod
     | 'ReturnedVoidMethod' methodName parameters                                  #ReturnedVoidMethod
     | 'ReturnedBooleanMethod' methodName parameters booleanValue                  #ReturnedBooleanMethod
     | 'ThrownException' className                                                 #ThrownException
     | 'SynchronizedStaticMethod' methodName parameters                            #SynchronizedStaticMethod
     ;

/* Boolean value */

booleanValue
     : 'true'
     | 'false'
     ;

/* Name of classes, methods, and fields */

className 
     : fullyQualifiedName 
     ;

methodName
     : className '.' IDENTIFIER
     ;

fieldName
     : className '.' IDENTIFIER
     ;

variableName
     : IDENTIFIER
     ;

/* Types */

type
     : primitiveType
     | className
     | type ('[' ']')+ // array type
     ;

primitiveType
     : numericType
     | 'boolean'
     ;

numericType
     : integralType
     | floatingPointType 
     ;

integralType
     : 'byte'
     | 'short'
     | 'int'
     | 'long'
     | 'char'
     ;

floatingPointType
     : 'float'
     | 'double'
     ;

/* Fully qualified names */

fullyQualifiedName
     : IDENTIFIER
     | packageName '.' IDENTIFIER
     ;

packageName
     : IDENTIFIER
     | packageName '.' IDENTIFIER
     ;

/* Parameters */ 

parameters
     : '(' parameterList? ')'
     ;

parameterList
     : type (',' type)*
     ;

/* Reserved words */

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

/* Identifiers */

IDENTIFIER
     : IdentifierStart IdentifierPart*
     ;

fragment IdentifierPart
     : IdentifierStart
     | [0-9]
     ;

fragment IdentifierStart
     : [a-zA-Z$_] // these are the "Java letters" below 0x7F
     | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
     | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
     ;

/* Skip whitespace and comments */

WS:           [ \t\r\n\u000C]+  -> skip;
COMMENT:      '/*' .*? '*/'     -> skip;
LINE_COMMENT: '//' ~[\r\n]*     -> skip;
