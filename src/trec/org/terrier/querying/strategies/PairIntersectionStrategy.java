package org.terrier.querying.strategies;

import org.terrier.querying.strategies.IntersectionStrategy;
import java.util.ArrayList;

public class PairIntersectionStrategy implements IntersectionStrategy{

    public String expandQuery(String query){
      StringBuilder newQuery = new StringBuilder();
      String[] tokens = query.split(" ");
      if (tokens.length == 1){
        newQuery.append(tokens[0]);
      } else {
          for (int i=0; i<tokens.length/2;i++){
              newQuery.append("("+tokens[2*i]+" AND "+tokens[(2*i)+1]+")");
          }
          if (!((tokens.length % 2) == 0)){ // cantidad impares de terminos?
              newQuery.append(" AND "+tokens[tokens.length-1]);
          }
      }
      return newQuery.toString();
    }

}
