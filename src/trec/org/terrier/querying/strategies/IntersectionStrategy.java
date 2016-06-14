package org.terrier.querying.strategies;

import java.util.ArrayList;

public interface IntersectionStrategy{

  /* Receive a query and return a list of conjunctive queries based on a particular strategy */
  String expandQuery(String query);

}
