// This software has been placed in the public domain by its author

import java.util.LinkedHashMap;
import java.util.Map;

class FifoNode extends Node
{
	FifoMap<Double,Double> cache = new FifoMap<Double,Double>();
	
	boolean cacheGet (double key)
	{
		return cache.containsKey (key);
	}
	
	void cachePut (double key)
	{
		cache.put (key, null);
	}
	
	class FifoMap<Key,Value> extends LinkedHashMap<Key,Value>
	{
		FifoMap()
		{
			super (CACHE_SIZE, 0.75f, false);
		}
		
		protected boolean removeEldestEntry (Map.Entry<Key,Value> entry)
		{
			return size() > CACHE_SIZE;
		}
	}
}
