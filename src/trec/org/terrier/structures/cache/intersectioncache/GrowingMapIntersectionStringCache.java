package org.terrier.structures.cache.intersectioncache;

public class GrowingMapIntersectionStringCache extends GrowingMapIntersectionCache {
	@Override
	protected String hashQuery(String q) {
		return q;
	}
}