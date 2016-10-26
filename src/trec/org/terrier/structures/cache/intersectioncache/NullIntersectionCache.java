

package org.terrier.structures.cache.intersectioncache;

import java.util.*;
import java.lang.Integer;

/** Do nothing QueryResultCache */
public class NullIntersectionCache implements IntersectionCache {
	public void reset(){}
	public void add(String q, List<Integer> intersectionResultset) {}
	public  List<Integer> checkCache(String q) {
		return null;
	}		
}
