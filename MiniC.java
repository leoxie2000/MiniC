import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.tool.DOTGenerator;
import java.util.List;
import java.util.*;


public class MiniC {
  
  public static void main( String[] args) throws Exception 
  {
    MiniCLexer lexer = new MiniCLexer( new ANTLRFileStream(args[0]));
    CommonTokenStream tokens = new CommonTokenStream( lexer );
    MiniCParser parser = new MiniCParser( tokens );
    parser.removeErrorListeners();
    parser.addErrorListener(new MiniCErrorListener());
    ParseTree tree = parser.program();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk( new MiniCWalker(), tree );

    List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
    String outputTree = TreeUtils.outputTree(tree, ruleNamesList);

    System.out.println(outputTree);
  }
}


