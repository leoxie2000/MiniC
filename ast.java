// COSC57 AST class for Java
// By Ben Kallus

import java.util.ArrayList;
import java.util.Optional;


// Relational operators
enum ASTROpType {
    LT,  // <
    GT,  // >
    LEQ,  // <=
    GEQ,  // >=
    EQ,  // ==
    NEQ;  // !=
}

// Binary operators
enum ASTBOpType {
    ADD,  // +
    SUB,  // -
    DIV,  // /
    MUL;  // *
}

// Unary operators
enum ASTUOpType {
    POS,  // +
    NEG;  // -
}

// The types in miniC
enum ASTDataType {
    VOID_T,
    INT_T,
    FLOAT_T,
    BOOL_T;
}


// Abstract class from which all node classes in the AST derive.
abstract class ASTNode {
    public abstract void print(int n);
    public void print() {
        print(0);
    }
    String INDENT_STR = "|   ";
}


// Abstract class from which all statements derive.
// A statement is (informally) anything that could sit on its own in a block.
abstract class ASTStmtNode extends ASTNode {}


// Abstract class from which variable and function declarations derive.
abstract class ASTDeclNode extends ASTStmtNode {}


// Abstract class from which all expressions derive.
// An expression is a term, a function call, or expressions combined with appropriate operators.
abstract class ASTExprNode extends ASTStmtNode {}


// An assignment (eg. "a = 1;")
class ASTAsgnNode extends ASTStmtNode {
    public String lhs;
    public ASTExprNode rhs;

    public ASTAsgnNode(String lhs_p, ASTExprNode rhs_p) {
        // The left-hand side of the assignment
        lhs = lhs_p;
        // The right-hand side of the assignment
        rhs = rhs_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sAsgn. %s =%n", INDENT_STR.repeat(indentation_level), lhs);
        rhs.print(indentation_level + 1);
    }
}


// A block (eg. "{ int a; a = 10 + b; exit(1); return will_never_get_here; }")
class ASTBlockNode extends ASTStmtNode {
    public ArrayList<ASTStmtNode> stmt_list;

    public ASTBlockNode(ArrayList<ASTStmtNode> stmt_list_p) {
        // The statements that make up the block
        stmt_list = stmt_list_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sBlock.%n", INDENT_STR.repeat(indentation_level));
        for (ASTStmtNode stmt : stmt_list) {
            stmt.print(indentation_level + 1);
        }
    }
}


// A function definition (eg. "int the_identity_fn(int n) { return n; }")
class ASTFuncDefNode extends ASTNode {
    public ASTDataType return_type;
    public String name;
    public ASTDataType param_type;
    public String param_name;
    public ASTBlockNode body;

    public ASTFuncDefNode(ASTDataType return_type_p, String name_p, ASTDataType param_type_p, String param_name_p, ASTBlockNode body_p) {
        // The return type of the function
        return_type = return_type_p;
        // The name of the function
        name = name_p;
        // The parameter type of the function
        param_type = param_type_p;
        // The parameter name of the function
        param_name = param_name_p;  // If there is no parameter name, just use ""
        // The function body
        body = body_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sFuncDef. %s %s(%s %s)%n", INDENT_STR.repeat(indentation_level), return_type, name, param_type, param_name);
        body.print(indentation_level + 1);
    }
}


// A root node of an AST. You should have one of these per compilation unit.
class ASTRootNode extends ASTNode {
    public ArrayList<ASTDeclNode> decls;
    public ArrayList<ASTAsgnNode> asgns;
    public ArrayList<ASTFuncDefNode> funcs;

    public ASTRootNode(ArrayList<ASTDeclNode> decls_p, ArrayList<ASTAsgnNode> asgns_p, ArrayList<ASTFuncDefNode> funcs_p) {
        // List of function and variable declarations.
        // Must be at the top of the file.
        decls = decls_p;
        // List of variable assignments for global variables.
        // Must be after declarations and before functions in the file.
        asgns = asgns_p;
        // List of function definitions.
        // Must be at the end of the file.
        funcs = funcs_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sRoot.%n", INDENT_STR.repeat(indentation_level));
        if (asgns.isEmpty()) {
            System.out.printf("%sAssignments:%n", INDENT_STR.repeat(indentation_level));
        }
        for (ASTAsgnNode asgn : asgns) {
            asgn.print(indentation_level + 1);
        }
        if (decls.isEmpty()) {
            System.out.printf("%sDeclarations:%n", INDENT_STR.repeat(indentation_level));
        }
        for (ASTDeclNode decl : decls) {
            decl.print(indentation_level + 1);
        }
        if (funcs.isEmpty()) {
            System.out.printf("%sFunctions:%n", INDENT_STR.repeat(indentation_level));
        }
        for (ASTFuncDefNode func : funcs) {
            func.print(indentation_level + 1);
        }
    }
}


// A variable declaration. (eg. "int a;")
class ASTVarDeclNode extends ASTDeclNode {
    public Boolean is_extern;
    public ASTDataType data_type;
    public String name;

    public ASTVarDeclNode(Boolean is_extern_p, ASTDataType data_type_p, String name_p) {
        // Whether the variable is extern
        is_extern = is_extern_p;
        // The type of the variable
        data_type = data_type_p;
        // The name of the variable
        name = name_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sVarDecl. %s%s %s%n", INDENT_STR.repeat(indentation_level), is_extern ? "extern " : "", data_type, name);
    }
}


// A function declaration (eg. "int main(void);" or "extern int square(int);")
class ASTFuncDeclNode extends ASTDeclNode {
    public Boolean is_extern;
    public ASTDataType return_type;
    public String name;
    public ASTDataType param_type;

    public ASTFuncDeclNode(Boolean is_extern_p, ASTDataType return_type_p, String name_p, ASTDataType param_type_p) {
        // Whether the function is extern
        is_extern = is_extern_p;
        // The return type of the function
        return_type = return_type_p;
        // The name of the function
        name = name_p;
        // The type of the parameter to the function
        param_type = param_type_p;
        // Note that parameter names don't matter in C function declarations because all arguments are positional.
        // Thus, we don't store that info in the AST.
    }

    public void print(int indentation_level) {
        System.out.printf("%sFuncDecl. %s %s %s(%s)%n", INDENT_STR.repeat(indentation_level), is_extern ? "extern " : "", return_type, name, param_type);
    }
}


// A variable term (eg. the "a" in "b = 10 + a * 2.1;")
class ASTVarNode extends ASTExprNode {
    public String name;

    public ASTVarNode(String name_p) {
        // The variable's name
        name = name_p;
        // Note that we don't know the variable's type here.
        // We'll get to that when we decorate the AST.
    }

    public void print(int indentation_level) {
        System.out.printf("%sVar. %s%n", INDENT_STR.repeat(indentation_level), name);
    }
}


// An integer constant term (eg. the "10" in "b = 10 + a * 2.1;")
class ASTIntLiteralNode extends ASTExprNode {
    public int value;

    public ASTIntLiteralNode(int value_p) {
        // The constant's value
        value = value_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sIntLiteral. %s%n", INDENT_STR.repeat(indentation_level), value);
    }
}


// A floating point constant term (eg. the "2.1" in "b = 10 + a * 2.1;")
class ASTFloatLiteralNode extends ASTExprNode {
    public float value;

    public ASTFloatLiteralNode(float value_p) {
        // The constant's value
        value = value_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sFloatLiteral. %s%n", INDENT_STR.repeat(indentation_level), value);
    }
}


// A boolean constant term (eg. "false")
class ASTBoolLiteralNode extends ASTExprNode {
    public Boolean value;

    public ASTBoolLiteralNode(Boolean value_p) {
        // The constant's value
        value = value_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sBoolLiteral. %s%n", INDENT_STR.repeat(indentation_level), value);
    }
}


// A relational expression (eg. "10 > 2")
class ASTRExprNode extends ASTExprNode {
    public ASTExprNode lhs;
    public ASTExprNode rhs;
    public ASTROpType op;

    public ASTRExprNode(ASTExprNode lhs_p, ASTExprNode rhs_p, ASTROpType op_p) {
        // The left-hand side of the relational operator
        lhs = lhs_p;
        // The right-hand side of the relational operator
        rhs = rhs_p;
        // The relational operator
        op = op_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sRExpr. %s%n", INDENT_STR.repeat(indentation_level), op);
        System.out.printf("%s(RExpr) LHS =%n", INDENT_STR.repeat(indentation_level));
        lhs.print(indentation_level + 1);
        System.out.printf("%s(RExpr) RHS =%n", INDENT_STR.repeat(indentation_level));
        rhs.print(indentation_level + 1);
    }
}


// A binary expression (eg. "10 + 2")
class ASTBExprNode extends ASTExprNode {
    public ASTExprNode lhs;
    public ASTExprNode rhs;
    public ASTBOpType op;

    public ASTBExprNode(ASTExprNode lhs_p, ASTExprNode rhs_p, ASTBOpType op_p) {
        // The left-hand side of the binary operator
        lhs = lhs_p;
        // The right-hand side of the binary operator
        rhs = rhs_p;
        // The binary operator
        op = op_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sBExpr. %s%n", INDENT_STR.repeat(indentation_level), op);
        System.out.printf("%s(BExpr) LHS =%n", INDENT_STR.repeat(indentation_level));
        lhs.print(indentation_level + 1);
        System.out.printf("%s(BExpr) RHS =%n", INDENT_STR.repeat(indentation_level));
        rhs.print(indentation_level + 1);
    }
}


// A unary expression (eg. "-10")
class ASTUExprNode extends ASTExprNode {
    public ASTExprNode expr;
    public ASTUOpType op;

    public ASTUExprNode(ASTExprNode expr_p, ASTUOpType op_p) {
        // The expression to which the unary operation applies
        expr = expr_p;
        // The unary operator
        op = op_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sUExpr. %s, expression =%n", INDENT_STR.repeat(indentation_level), op);
        expr.print(indentation_level + 1);
    }
}


// A function call (eg. "set_coolness_factor(100);")
class ASTCallNode extends ASTExprNode {
    public String name;
    public Optional<ASTExprNode> param;

    public ASTCallNode(String name_p, Optional<ASTExprNode> param_p) {
        // The name of the function being called
        name = name_p;
        // The parameter to the function
        param = param_p;  // Remember this is optional
    }

    public void print(int indentation_level) {
        System.out.printf("%sCall. %s%s%n", INDENT_STR.repeat(indentation_level), name, param.isPresent() ? ", parameter =" : "");
        if (param.isPresent()) {
            param.get().print(indentation_level + 1);
        }
    }
}


// A return statement (eg. "return 10;")
class ASTRetNode extends ASTStmtNode {
    public Optional<ASTExprNode> expr;

    public ASTRetNode(Optional<ASTExprNode> expr_p) {
        // The thing being returned
        expr = expr_p;  // Remember this is optional
    }

    public void print(int indentation_level) {
        System.out.printf("%sRet.%s%n", INDENT_STR.repeat(indentation_level), expr.isPresent() ? " expression =" : "");
        if (expr.isPresent()) {
            expr.get().print(indentation_level + 1);
        }
    }
}


// A while statement (eg. "while (1 == 1) { 1; }")
class ASTWhileNode extends ASTStmtNode {
    public ASTExprNode cond;
    public ASTBlockNode body;

    public ASTWhileNode(ASTExprNode cond_p, ASTBlockNode body_p) {
        // The loop condition
        cond = cond_p;
        // The loop body
        body = body_p;
    }

    public void print(int indentation_level) {
        System.out.printf("%sWhile.%n", INDENT_STR.repeat(indentation_level));
        System.out.printf("%s(While) condition =%n", INDENT_STR.repeat(indentation_level));
        cond.print(indentation_level + 1);
        System.out.printf("%s(While) body =%n", INDENT_STR.repeat(indentation_level));
        body.print(indentation_level + 1);
    }
}


// An if statement (eg. "if (1 != 1) { return BAD_NEWS; } else { return ALL_IS_WELL; }")
class ASTIfNode extends ASTStmtNode {
    public ASTExprNode cond;
    public ASTBlockNode if_body;
    public ASTBlockNode else_body;

    public ASTIfNode(ASTExprNode cond_p, ASTBlockNode if_body_p, Optional<ASTBlockNode> else_body_p) {
        // The if condition
        cond = cond_p;
        // The if body
        if_body = if_body_p;
        // The else body
        else_body = else_body_p.isPresent() ? else_body_p.get() : new ASTBlockNode(new ArrayList<ASTStmtNode>());
    }

    public void print(int indentation_level) {
        System.out.printf("%sIf.%n", INDENT_STR.repeat(indentation_level));
        System.out.printf("%s(If) condition =%n", INDENT_STR.repeat(indentation_level));
        cond.print(indentation_level + 1);
        System.out.printf("%s(If) if_body =%n", INDENT_STR.repeat(indentation_level));
        if_body.print(indentation_level + 1);
        System.out.printf("%s(If) else_body =%n", INDENT_STR.repeat(indentation_level));
        else_body.print(indentation_level + 1);
    }
}


// An empty statement (eg. ";")
// (This could just be ASTStmtNode, but I'd rather have that be an abstract class)
class ASTEmptyStmtNode extends ASTStmtNode {
    public ASTEmptyStmtNode() {}

    public void print(int indentation_level) {
        System.out.printf("%sEmptyStmt.%n", INDENT_STR.repeat(indentation_level));
    }
}
