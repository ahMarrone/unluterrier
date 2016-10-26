package org.terrier.querying;



import org.terrier.structures.Index;
import org.terrier.structures.PostingIndex;
import org.terrier.structures.bit.InvertedIndex;
import org.terrier.structures.MetaIndex;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.postings.IterablePosting;
import org.terrier.structures.BitIndexPointer;
import org.terrier.matching.QueryResultSet;
import org.terrier.querying.parser.Query;
import org.terrier.querying.parser.MultiTermQuery;
import org.terrier.matching.models.BooleanModel;

import java.lang.NullPointerException;
import java.io.IOException;


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
    Queue<String> queryPostfix = BooleanManager.toPostfix(query);
    List<Integer> docIds = this.match(queryPostfix);
    int[] values = this.getArrayDocIds(docIds);
    double[] doubles = new double[values.length];
    short[] shorts = new short[values.length];
    QueryResultSet resultset = new QueryResultSet(values, doubles, shorts);
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



  // Run boolean matching
  // Takes a postfix query (queue), and return a resultset of doc id's
  private List<Integer> match(Queue<String> queryPostfix){
      // Stack of temporary results. RPN
      Deque<List> outStack = new ArrayDeque<List>();
      //
      List<Integer> matchResult = null;
      List<Integer> tmpResult = null;
      List<Integer> first = null;
      List<Integer> second = null;
      for (String token : queryPostfix){
        if (BooleanManager.isBooleanOperator(token)){
          switch (token){
            case "AND" :
                      first = this.getPostingIfString(outStack.pop());
                      second = this.getPostingIfString(outStack.pop());  
                      tmpResult = (first.size() <= second.size()) ? BooleanModel.doAND(first,second) : BooleanModel.doAND(second, first); 
                      break;
            case "OR"  :
                      first = this.getPostingIfString(outStack.pop());
                      second = this.getPostingIfString(outStack.pop());
                      tmpResult = BooleanModel.doOR(first, second);
                      break;
          }
          outStack.push(tmpResult);
        } else {
          outStack.push(this.getPostingList(token));
        }
      }
      if (outStack.size() > 1 ){ // Incorrect Query
          System.out.println(("WRONG QUERY!"));
          matchResult =  new ArrayList();
      } else {
        matchResult = outStack.pop();
      }
      return matchResult;
  }


  private List<Integer> getPostingIfString(Object term){
      if(term instanceof String){
          term = this.getPostingList((String)term);
      }
      return (List<Integer>)term;
  }


  private List<Integer> getPostingList(String term){
      List<Integer> postingDocs = new ArrayList();
      Index index = this.getIndex();
      PostingIndex inv = index.getInvertedIndex();
      MetaIndex meta = index.getMetaIndex();
      Lexicon<String> lex = index.getLexicon();
      LexiconEntry le = lex.getLexiconEntry(term);
      try {
        IterablePosting postings = inv.getPostings((BitIndexPointer) le);
        while (postings.next() != IterablePosting.EOL) {
          String docno = meta.getItem("docno", postings.getId());
          postingDocs.add(Integer.valueOf(docno));
        }
      } catch (NullPointerException npe){
        // Term does'nt exists in lexicon
      } catch (IOException e){
        System.out.println("ERROR - Error reading Posting List of term" + term);
      } finally {
        return postingDocs;
      }
  }



  // Converts a infix string into postfix notation (boolean strings)
  public static Queue<String> toPostfix(String inString){
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
