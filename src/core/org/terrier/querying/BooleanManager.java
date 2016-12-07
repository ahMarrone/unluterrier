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

import org.terrier.utility.ApplicationSetup;
import org.terrier.structures.cache.intersectioncache.IntersectionCache;
import org.terrier.structures.cache.intersectioncache.NullIntersectionCache;
import com.google.common.cache.CacheLoader;

import java.lang.NullPointerException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.lang.Integer;

public class BooleanManager extends Manager{


  /** The logger used */
  protected static final Logger logger = LoggerFactory.getLogger(BooleanManager.class);

  protected IntersectionCache intersectionCache;

  private String cacheStringQuery = null;

  public BooleanManager(Index _index){
    super(_index);
    this.intersectionCache = getIntersectionCache();
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

  protected IntersectionCache getIntersectionCache() {
    IntersectionCache rtr = null;
    try {
      String className = ApplicationSetup.getProperty(
          "trec.querying.intersectioncache", NullIntersectionCache.class
              .getName());
      if (!className.contains("."))
        className = "org.terrier.applications.TRECQuerying$"
            + className;
      else if (className.startsWith("uk.ac.gla.terrier"))
        className = className.replaceAll("uk.ac.gla.terrier", "org.terrier");
      rtr = Class.forName(className).asSubclass(IntersectionCache.class).newInstance();
    } catch (Exception e) {
      logger.error("", e);
    }
    return rtr;
  }



  ////////////////////////////////////////////////////////////////

  public enum BOOLEAN_OPERATORS {
      AND, OR;
  }



  // Run boolean matching
  // Takes a postfix query (queue), and return a resultset of doc id's
  private List<Integer> match(Queue<String> queryPostfix){
      // Stack of temporary results. RPN
      Deque<Object> outStack = new ArrayDeque<Object>();
      //
      List<Integer> matchResult = null;
      List<Integer> tmpResult = null;
      Object first = null;
      Object second = null;
      for (String token : queryPostfix){
        if (BooleanManager.isBooleanOperator(token)){
          switch (token){
            case "AND" :
                      first = outStack.pop();
                      second = outStack.pop();
                      tmpResult = this.checkIntersectionCache(first, second);
                      if (tmpResult == null){
                        List<Integer> firstPosting = this.getPostingIfString(first);
                        List<Integer> secondPosting = this.getPostingIfString(second);  
                        tmpResult = (firstPosting.size() <= secondPosting.size()) ? BooleanModel.doAND(firstPosting,secondPosting) : BooleanModel.doAND(secondPosting, firstPosting); 
                        this.addEntryToIntersectionCache(first, second, tmpResult);
                      }
                      break;
            case "OR"  :
                      /*first = this.getPostingIfString(outStack.pop());
                      second = this.getPostingIfString(outStack.pop());
                      tmpResult = BooleanModel.doOR(first, second);*/
                      break;
          }
          outStack.push(tmpResult);
        } else {
          outStack.push(token);
        }
        //System.out.println(outStack);
      }
      if (outStack.size() > 1 ){ // Incorrect Query
          logger.info("WRONG QUERY");
          matchResult =  new ArrayList();
      } else {
        // Aqui el stack tiene un elemento, el cual puede ser un termino, o una postinglist.
        matchResult = this.getPostingIfString(outStack.pop());
      }
      System.out.println("INTERSECTION CACHE HITS: " + this.intersectionCache.getHitCount());
      System.out.println("INTERSECTION CACHE HIT RATIO: " + this.intersectionCache.getHitRatio());
      System.out.println("INTERSECTION CACHE ENTRIES: " + this.intersectionCache.getNumberOfEntries());
      return matchResult;
  }


  private List<Integer> checkIntersectionCache(Object term1, Object term2){
      // REVISAR! casa AND perro y casa AND perro son dos entradas distintas en la cache!
      if((term1 instanceof String) && (term2 instanceof String)){
          String cacheStringQuery = this.constructCacheStringQuery((String)term1, (String)term2);
          List<Integer> result = this.intersectionCache.checkCache(cacheStringQuery);
          if (result != null){
            logger.info("INTERSECTION CACHE HIT!: "+ cacheStringQuery);
            return result;
          }
      }
      return null;
  }

  private String constructCacheStringQuery(String t1, String t2){
      String stringResult = null;    
      if (t1.compareTo(t2) < 0){
        stringResult = t1 + " AND " + t2;
      } else {
        stringResult = t2 + " AND " + t1;
      }
      return stringResult;
  }

  private void addEntryToIntersectionCache(Object term1, Object term2, List<Integer> value){
    if((term1 instanceof String) && (term2 instanceof String)){
      this.intersectionCache.add(this.constructCacheStringQuery((String)term1, (String)term2), value);
    }
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
         // El termino no existe en el lexicon
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
