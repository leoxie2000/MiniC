import java.util.*;

import javax.print.event.PrintEvent;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
public class MiniCErrorListener extends BaseErrorListener {
    public static boolean hasError = false;
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e)
        {
        hasError = true;
        List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
        Collections.reverse(stack);

        Token token = (Token) offendingSymbol;
        
        System.err.println("Token "+ "\""+token.getText() + "\""+ " (line "+line+", column "+(charPositionInLine+1)+")"+
                            offendingSymbol+": "+msg);

        }
    
    }