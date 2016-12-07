

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

	public float getHitRatio(){
		return -1;
	}

	public int getHitCount(){
		return -1;
	}	



	public int getNumberOfEntries(){
		return -1;
	}	

}
