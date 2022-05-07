import java.util.HashMap;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;

public class MiniCWalker extends MiniCBaseListener {
  HashMap<String, Boolean> varDeclared;

  boolean hasError = false;
  public void enterProgram(MiniCParser.ProgramContext ctx){
      System.out.println("Entering Program\n");
  }

  public void enterFunc_decl(MiniCParser.Func_declContext ctx){
      //reset hashmap everytime we enter new function for sake of scope
      varDeclared = new HashMap<>();
  }
  public void enterParam(MiniCParser.ParamContext ctx){
    //Adding function input string into hashmap
    String temp = ctx.NAME().getText();
    if(!varDeclared.containsKey(temp)){
      varDeclared.put(temp, true);
    }


  }
  public void enterLocal_decl(MiniCParser.Local_declContext ctx) {
    //getting the local variable name declaration
    String temp = ctx.NAME().getText();

    //prints error for multiple declaration
    if(varDeclared.containsKey(temp)){
      System.out.println("Multiple declaration of variable : " + temp + "\n");
    }
    else{
      //if undeclared before, add to hashmap
      varDeclared.put(temp, true);
    }
  }

  public void enterExpr(MiniCParser.ExprContext ctx){
    String temp = "Nothing";
    //Trying to make sure we are at a variable name
    try{
      temp = ctx.NAME().getText();
    }
    catch(Exception e){
      //This means we are not at a NAME token, so ignore
    }

    if(temp != "Nothing"){
      //We arrived at token of variable name, check for undeclared version
      if(!varDeclared.containsKey(temp)){
        System.out.println("Undeclared Variable " + temp + "\n");
      }
      
    }

  }
  @Override public void visitErrorNode(ErrorNode node){
    hasError = true;
  }
  public void enterError(MiniCParser.ErrorContext ctx){
    System.out.println("Identifying error at: " + ctx.getText() +"\n");
  }
  
  public void exitProgram(MiniCParser.ProgramContext ctx){
    System.out.println("Exiting Program \n");
  }
}
