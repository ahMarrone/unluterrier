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
    q.setOriginalQuery(query);
    return q;
  }


  public void runPreProcessing(SearchRequest srq){
    //System.out.println("PREPROCESSING!");
  }

  public void runMatching(SearchRequest srq){
    Request rq = (Request)srq;
    String query = rq.getOriginalQuery();
    Queue queryPostfix = ShuntingYard.toPostfix(query);
    rq.setResultSet(new QueryResultSet(1));
  }



  public void runPostFilters(SearchRequest srq){
    //System.out.println("POSTFILTERS!");
  }
}
