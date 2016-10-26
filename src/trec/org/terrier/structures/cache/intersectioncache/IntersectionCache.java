package org.terrier.structures.cache.intersectioncache;


import java.util.*;
import java.lang.Integer;

public interface IntersectionCache {

	List<Integer> checkCache(final String q);

	void add(String q, List<Integer> intersectionResultset);
	
	void reset();
}