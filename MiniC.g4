grammar MiniC;
options { 
  output=AST; 
}
program         : decl+
                    ;

decl            : func_decl+
                    ;
//source is optional: external
// compound can either be code stuff or a ending semi-colon.

func_decl       : type NAME '(' parameters ')' compound
                    ;

type            : EXTERN VOID 
                    | EXTERN INT 
                    | VOID 
                    | INT
                    ;

//parameters can be either one or multiple statements of params (int i, int j), or emty, or void
parameters      : param(',' param)*
                    | VOID
                    | INT
                    | 
                    ;

//a specific parameter should take the form int i or void j.
param           : type NAME;

//if the compound is ";", it means the declaration is at top of the file
// otherwise this is a real function with real code and we proceed to parse
compound        : ';'
                    | '{' local_decl* stmt* '}'
                    ;
local_decl      : type NAME ';'; 
stmt            : expr_stmt
                    | if_stmt
                    | while_stmt
                    | return_stmt
                    | compound
                    ;
expr_stmt       : expr ';'
                    ;
if_stmt         : IF '(' r_expr ')' stmt
                    | IF '(' r_expr ')' stmt ELSE stmt
                    ;
while_stmt      : WHILE '(' r_expr ')' stmt
                    ;
//can either just return or return an expression
return_stmt     : RETURN ';'
                    | RETURN expr ';'
                    ;
r_expr          :  expr LEQ expr
                    | expr GEQ expr
                    | expr EQ expr
                    | expr NEQ expr
                    | expr '>' expr
                    | expr '<' expr
                    | expr '=' expr{notifyErrorListeners("Relational error, '=' is not relational\n");}
                    ;
expr            :   NAME 
                    | CONSTANT      
                    | expr '+' expr
                    | expr '-' expr
                    | expr '*' expr
                    | expr '/' expr
                    | assignment
                    | '-' expr //unary
                    | '(' expr ')' //multiple layers of parenthesis
                    | func_call
                    ;
//an expression is a congregate of possibilities
assignment      : NAME '=' expr ;
func_call       : NAME '(' call_param ')'
                    ;
call_param      : expr (','expr)*
                    ;
error           : ERROR;
EXTERN          : 'extern' ;
VOID            : 'void';
INT             : 'int' ;
WHILE           : 'while';
IF              : 'if';
ELSE            : 'else';
RETURN          : 'return';
LEQ             : '<=';
GEQ             : '>=';
EQ              : '==';
NEQ             : '!=';
NAME            : [a-zA-Z][a-zA-Z0-9_]*;
CONSTANT        : '0' 
                    | [1-9][0-9]*
                    ;
WS					: (' '|'\t'|'\r'|'\n')+
					-> channel(HIDDEN)
					;
ERROR           : .;


