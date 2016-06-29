package org.terrier.querying.rpn;

import java.util.*;




public class ShuntingYard{


  public enum BOOLEAN_OPERATORS {
      AND, OR;
  }

  // Converts a infix string into postfix notation (boolean strings)
  public static String toPostfix(String inString){
    Deque<String> tmpStack = new ArrayDeque<String>(); // algorithm stack
    StringBuilder outString = new StringBuilder(); // out string. Queue as string.
    //Queue<String> outQueue = new LinkedList<String>();

    for (String token : inString.split("\\s")) {
      if (ShuntingYard.isBooleanOperator(token)){
        while (ShuntingYard.isBooleanOperator((String)tmpStack.peek())){ // top of stack is operator?
            outString.append(tmpStack.pop()).append(" ");
            //outQueue.add(tmpStack.pop());
        }
        tmpStack.push(token);
      } else if (token.equals(")")){
        while (!tmpStack.peek().equals("(")){
          outString.append(tmpStack.pop()).append(" ");
          //outQueue.add(tmpStack.pop());
        }
        tmpStack.pop();
      } else if (token.equals("(")){
        tmpStack.push(token);
      } else { // raw token. vocabulary term
        outString.append(token).append(" ");
        //outQueue.add(token);
      }
    }
    while (!tmpStack.isEmpty()){
      outString.append(tmpStack.pop()).append(" ");
      //outQueue.add(tmpStack.pop());
    }
    return outString.toString();
    //return outQueue;
  }


  public static boolean isBooleanOperator(String op) {
      for (BOOLEAN_OPERATORS boolOp : BOOLEAN_OPERATORS.values()) {
          if (boolOp.name().equals(op)) {
              return true;
          }
      }

      return false;
  }

}
