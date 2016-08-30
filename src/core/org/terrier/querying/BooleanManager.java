package org.terrier.querying;

import org.terrier.structures.Index;
import org.terrier.matching.QueryResultSet;
import org.terrier.querying.parser.Query;
import org.terrier.querying.parser.MultiTermQuery;
import org.terrier.querying.parser.LazyQueryParser;
import org.terrier.querying.parser.QueryParserException;

import java.util.Queue;
import org.terrier.querying.rpn.ShuntingYard;

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
    try{
      System.out.println("pre parse");
      System.out.println(query);
      LazyQueryParser.parseQuery(query, q);	 // CAMBIO
    } catch (QueryParserException qpe) {
      logger.error("Error while parsing the query.",qpe);
    }
    q.setOriginalQuery(query);
    return q;
  }


  public void runPreProcessing(SearchRequest srq){
    //System.out.println("PREPROCESSING!");
  }

  public void runMatching(SearchRequest srq){
    Request rq = (Request)srq;
    Query q = rq.getQuery();
    System.out.println(q.parseTree());
    System.out.println("matching!!!");
    rq.setResultSet(new QueryResultSet(0));
  }



  public void runPostFilters(SearchRequest srq){
    //System.out.println("POSTFILTERS!");
  }
}
