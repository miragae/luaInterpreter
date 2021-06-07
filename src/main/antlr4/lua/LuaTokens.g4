lexer grammar LuaTokens;

// arithmetic
ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
POW : '^' ;
MOD : '%' ;

// relational
EQ  : '==' ;
NEQ : '~=' ;
GT  : '>' ;
GTE : '>=' ;
LT  : '<' ;
LTE : '<=' ;

// logical
AND : 'and' ;
OR  : 'or' ;
NOT : 'not' ;

// other
ASSIGN : '=' ;
COLON  : ':' ;
DOT    : '.' ;
COMMA  : ',' ;
SEMI   : ';' ;
LEN    : '#' ;
CONCAT : '..' ;
LPAREN : '(' ;
RPAREN : ')' ;
LSQUARE: '[' ;
RSQUARE: ']' ;
LCURL  : '{' ;
RCURL  : '}' ;

// functions
PRINT  :  'print' ;
READ   :  'io.read' ;

// keywords
IF      : 'if' ;
THEN    : 'then' ;
ELSE    : 'else' ;
ELSEIF  : 'elseif' ;
END     : 'end';
DO      : 'do' ;
FOR     : 'for' ;
WHILE   : 'while' ;
BREAK   : 'break' ;
REPEAT  : 'repeat' ;
UNTIL   : 'until' ;
TRUE    : 'true' ;
FALSE   : 'false' ;
NIL     : 'nil' ;
IN      : 'in' ;
LOCAL   : 'local' ;
FUNC    : 'function' ;
RETURN  : 'return' ;


// comments
LINE_COMMENT : '--' ~[\r\n]* -> channel(HIDDEN);

// literals
NAME            : [a-zA-Z_][a-zA-Z_0-9]* ;
STRING          : '"' ~ ["\r\n]* '"' ; //add escape sequence
WS              : [ \t\r\n]+ -> skip ;

// numbers
INT : [0-9]+ ;
FLOAT : [0-9]+ '.' [0-9]+ ;