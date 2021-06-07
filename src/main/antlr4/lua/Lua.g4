grammar Lua;
import LuaTokens;

chunk : block EOF ;

block : stat* retStat? ;

stat
    : SEMI                                                          #semicolonStat
    | BREAK                                                         #breakStat
    | (LOCAL)? var ASSIGN exp                                       #assignStat
    | IF exp THEN block (ELSEIF exp THEN block)* (ELSE block)? END  #ifStat
    | (LOCAL)? FUNC NAME LPAREN argList? RPAREN block END           #funcDefStat
    | funcCall                                                      #funcCallStat
    | libCall                                                       #libCallStat
    | loop                                                          #loopStat
    ;

retStat : RETURN expList? SEMI? ;

funcCall : NAME LPAREN expList? RPAREN ;

libCall
    : PRINT LPAREN expList? RPAREN  #printCall
    | READ LPAREN string? RPAREN    #readCall
    ;

loop
    : DO block END                                                  #doLoop
    | WHILE LPAREN exp RPAREN DO block END                          #whileLoop
    | REPEAT block UNTIL LPAREN exp RPAREN                          #repeatLoop
    | FOR NAME ASSIGN exp COMMA exp (COMMA exp)? DO block END       #forLoop
    ;

var : NAME (tableIndex)* ;

exp
    : NIL                                           #nilExp
    | logical                                       #booleanExp
    | var                                           #varExp
    | number                                        #numberExp
    | string                                        #stringExp
    | tableConstructor                              #tableConstructorExp
    | exp op=(ADD | SUB | MUL | DIV | MOD) exp      #arithmeticOp
    | exp op=(EQ | NEQ | GT | GTE | LT | LTE) exp   #relationalOp
    | exp op=(AND | OR) exp                         #logicalOp
    | op=(NOT | SUB | LEN) exp                      #unaryOp
    | exp CONCAT exp                                #concatOp
    | <assoc=right> exp POW exp                     #powerOp
    | funcCall                                      #funcCallExp
    | libCall                                       #libCallExp
    ;

expList : exp (COMMA exp)* ;

argList : NAME (COMMA NAME)* ;

logical : FALSE | TRUE ;

number : INT | FLOAT ;

string : STRING ;

keyValue : (NAME EQ)? exp ;

keyValueList : keyValue (COMMA keyValue)* ;

tableIndex
            : DOT NAME
            | LSQUARE exp RSQUARE
            ;

tableConstructor : LCURL (keyValueList)? RCURL ;
