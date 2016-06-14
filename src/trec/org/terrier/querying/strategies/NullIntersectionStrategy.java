package org.terrier.querying.strategies;

import org.terrier.querying.strategies.IntersectionStrategy;
import java.util.ArrayList;

public class NullIntersectionStrategy implements IntersectionStrategy{

    public String expandQuery(String query){
      return query;
    }

}
