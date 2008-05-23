// This software has been placed in the public domain by its author

import java.util.LinkedHashMap;
import java.util.Map;

class LruNode extends Node
{
	LruMap<Double,Double> cache = new LruMap<Double,Double>();
	
	boolean cacheGet (double key)
	{
		// Use get() rather than containsKey() to update the LRU order
		return cache.get (key) != null;
	}
	
	void cachePut (double key)
	{
		cache.put (key, key);
	}
	
	class LruMap<Key,Value> extends LinkedHashMap<Key,Value>
	{
		LruMap()
		{
			super (CACHE_SIZE, 0.75f, true);
		}
		
		protected boolean removeEldestEntry (Map.Entry<Key,Value> entry)
		{
			return size() > CACHE_SIZE;
		}
	}
}
