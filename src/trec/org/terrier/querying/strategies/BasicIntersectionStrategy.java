package org.terrier.querying.strategies;

import org.terrier.querying.strategies.IntersectionStrategy;
import java.util.ArrayList;

public class BasicIntersectionStrategy implements IntersectionStrategy{

    public String expandQuery(String query){
      StringBuilder newQuery = new StringBuilder();
      String[] tokens = query.split(" ");
      int i = 0;
      if (tokens.length == 1){
        newQuery.append(tokens[0]);
      } else {
          while (i < tokens.length){
            if (i == 0){
              newQuery.append("(+"+tokens[i]+" +"+tokens[i+1]+")");
              i++;
            } else if (i == tokens.length-1) {
              newQuery.append(" +"+tokens[i]);
            } else {
              newQuery.insert(0,"(");
              newQuery.append(" +"+tokens[i]+")");
            }
            i++;
          }
      }
      return newQuery.toString();
    }

}
