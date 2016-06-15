package org.terrier.querying.strategies;

import org.terrier.querying.strategies.IntersectionStrategy;
import java.util.ArrayList;

public class OverlapIntersectionStrategy implements IntersectionStrategy{

    public String expandQuery(String query){
      StringBuilder newQuery = new StringBuilder();
      String[] tokens = query.split(" ");
      if (tokens.length == 1){
        newQuery.append(tokens[0]);
      } else {
          for (int i=0; i<tokens.length-1;i++){
              newQuery.append("("+tokens[i]+" AND "+tokens[i+1]+")");
          }
      }
      return newQuery.toString();
    }

}
