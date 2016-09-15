package org.terrier.querying;

import org.terrier.structures.Index;
import org.terrier.matching.QueryResultSet;
import org.terrier.querying.parser.Query;
import org.terrier.querying.parser.MultiTermQuery;
import org.terrier.querying.parser.LazyQueryParser;
import org.terrier.querying.parser.QueryParserException;
import org.terrier.matching.models.BooleanModel;

import java.util.*;
import java.lang.Integer;

public class BooleanManager extends Manager{

  public BooleanManager(Index _index){
    super(_index);
  }

  public SearchRequest newSearchRequest(String QueryID, String query)
  {
    Request q = new Request();
    if (Defaults_Size >0)
      setDefaults(q);
    q.setQueryID(QueryID);
    q.setIndex(this.index);
    q.setOriginalQuery(query);
    return q;
  }


  public void runPreProcessing(SearchRequest srq){
    //System.out.println("PREPROCESSING!");
  }

  public void runMatching(SearchRequest srq){
    Request rq = (Request)srq;
    String query = rq.getOriginalQuery();
    Queue queryPostfix = BooleanManager.toPostfix(query);
    List<Integer> docIds = BooleanModel.doAND("t1","t2");
    QueryResultSet resultset = new QueryResultSet(this.getArrayDocIds(docIds));
    rq.setResultSet(resultset);
  }



  public void runPostFilters(SearchRequest srq){
    //System.out.println("POSTFILTERS!");
  }



  ////////////////////////////////////////////////////////////////



  private int[] getArrayDocIds(List<Integer> integers){
    int[] ret = new int[integers.size()];
    Iterator<Integer> iterator = integers.iterator();
    for (int i = 0; i < ret.length; i++)
    {
        ret[i] = iterator.next().intValue();
    }
    return ret;
  }



  ////////////////////////////////////////////////////////////////



  public enum BOOLEAN_OPERATORS {
      AND, OR;
  }

  // Converts a infix string into postfix notation (boolean strings)
  public static Queue toPostfix(String inString){
    Deque<String> tmpStack = new ArrayDeque<String>(); // algorithm stack
    //StringBuilder outString = new StringBuilder(); // out string. Queue as string.
    Queue<String> outQueue = new LinkedList<String>();

    for (String token : inString.split("\\s")) {
      if (BooleanManager.isBooleanOperator(token)){
        while (BooleanManager.isBooleanOperator((String)tmpStack.peek())){ // top of stack is operator?
            //outString.append(tmpStack.pop()).append(" ");
            outQueue.add(tmpStack.pop());
        }
        tmpStack.push(token);
      } else if (token.equals(")")){
        while (!tmpStack.peek().equals("(")){
          //outString.append(tmpStack.pop()).append(" ");
          outQueue.add(tmpStack.pop());
        }
        tmpStack.pop();
      } else if (token.equals("(")){
        tmpStack.push(token);
      } else { // raw token. vocabulary term
        //outString.append(token).append(" ");
        outQueue.add(token);
      }
    }
    while (!tmpStack.isEmpty()){
      //outString.append(tmpStack.pop()).append(" ");
      outQueue.add(tmpStack.pop());
    }
    //return outString.toString();
    return outQueue;
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
