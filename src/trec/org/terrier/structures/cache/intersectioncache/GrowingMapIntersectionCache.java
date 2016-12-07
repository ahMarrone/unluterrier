package org.terrier.structures.cache.intersectioncache;

import java.util.HashMap;
import java.util.Map;

import java.util.*;
import java.lang.Integer;

/** an astract results cache that puts stuff into an ever-growing Map */
public abstract class GrowingMapIntersectionCache implements IntersectionCache 
{
	Map<String, List<Integer>> cache = new HashMap<String, List<Integer>>();

	private int hitCount = 0;
	private int hitAttempts = 0;

	public void reset()
	{
		cache.clear();
	}
	protected abstract String hashQuery(String q);
	
	public void add(String q, List<Integer> intersectionResultset)
	{
		cache.put(hashQuery(q), intersectionResultset);
	}		
	
	public List<Integer> checkCache(String q)
	{
		this.hitAttempts++;
		List<Integer> result = cache.get(hashQuery(q));
		if (result != null){
			this.hitCount++;
		}
		return result;
	}


	public float getHitRatio(){
		return (float) this.hitCount / this.hitAttempts;
	}

	public int getHitCount(){
		return this.hitCount;
	}

	public int getNumberOfEntries(){
		return cache.size();
	}

}