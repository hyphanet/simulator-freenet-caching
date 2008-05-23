// This software has been placed in the public domain by its author

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
	
abstract class Node implements Comparator<Node>
{
	public static int CACHE_SIZE = 1000, MAX_DEPTH = 10;
	public static Class CLASS = null; // Factory class
	
	// Factory method
	static Node create()
	{
		try {
			return (Node) CLASS.newInstance();
		}
		catch (Exception e) {
			System.err.println (e);
			System.exit (2);
			return null;
		}
	}
	
	double loc = Math.random(), target = 0.0;
	HashSet<Node> neighbours = new HashSet<Node>();
	HashSet<Double> store = new HashSet<Double>();
	
	abstract boolean cacheGet (double key);
	abstract void cachePut (double key);
	
	// Return the depth at which the request succeeded, or -1 if it failed
	int request (double key, int depth, HashSet<Node> visited)
	{
		if (!visited.add (this)) return -1; // Loop
		if (cacheGet (key) || store.contains (key)) return depth;
		if (depth == MAX_DEPTH) return -1; // DNF
		target = key; // Sort neighbours by closeness to requested key
		ArrayList<Node> nbrs = new ArrayList<Node> (neighbours);
		Collections.sort (nbrs, this);
		for (Node n : nbrs) {
			int d = n.request (key, depth + 1, visited);
			if (d != -1) {
				cachePut (key);
				return d;
			}
		}
		return -1; // RNF
	}
	
	public int compare (Node n1, Node n2)
	{
		double d1 = CachingSim.distance (n1.loc, target);
		double d2 = CachingSim.distance (n2.loc, target);
		if (d1 < d2) return -1;
		if (d1 > d2) return 1;
		return 0;
	}
	
	public boolean equals (Node n1, Node n2)
	{
		return compare (n1, n2) == 0;
	}
}
