// This software has been placed in the public domain by its author

class RandomNode extends Node
{
	double[] cache = new double[CACHE_SIZE];
	double salt = Math.random();
	
	boolean cacheGet (double key)
	{
		return cache[hash (key)] == key;
	}
	
	void cachePut (double key)
	{
		cache[hash (key)] = key;
	}
	
	int hash (double key)
	{
		// Throw away the low-order bits because Math.random() sucks
		long bits = Double.doubleToLongBits (key + salt) >> 8;
		return (int) (bits & 0x7FFFFFFF) % CACHE_SIZE;
	}
}
