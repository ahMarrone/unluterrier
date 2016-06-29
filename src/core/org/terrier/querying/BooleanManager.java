package org.terrier.querying;

import org.terrier.structures.Index;
import org.terrier.matching.QueryResultSet;

public class BooleanManager extends Manager{

  public BooleanManager(Index _index){
    super(_index);
  }

  public void runMatching(SearchRequest srq){
    System.out.println("Matching!");
    Request rq = (Request)srq;
    rq.setResultSet(new QueryResultSet(0));
  }



  public void runPostFilters(SearchRequest srq){
    System.out.println("POSTFILTERS!");
  }
}
