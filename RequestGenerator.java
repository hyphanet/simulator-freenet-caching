// This software has been placed in the public domain by its author

import java.util.ArrayList;
import java.util.TreeSet;

abstract class RequestGenerator
{
	public static Class CLASS = null; // Factory class
	
	TreeSet<Request> queue = new TreeSet<Request>();
	double now = 0.0;
	
	// Factory method
	static RequestGenerator create()
	{
		try {
			return (RequestGenerator) CLASS.newInstance();
		}
		catch (Exception e) {
			System.err.println (e);
			System.exit (2);
			return null;
		}
	}
	
	// Schedule the first request for each key
	void init (ArrayList<Double> keys)
	{
		for (int i = 0, size = keys.size(); i < size; i++) {
			double rate = requestRate();
			double time = now - Math.log (Math.random()) / rate;
			queue.add (new Request (keys.get (i), rate, time));
		}
	}
	
	// Return the rate at which a given key will be requested
	abstract double requestRate();
	
	// Return a key and schedule the next request for that key
	double generateRequest()
	{
		Request r = queue.first();
		queue.remove (r);
		now = r.time;
		double time = now - Math.log (Math.random()) / r.rate;
		queue.add (new Request (r.key, r.rate, time));
		return r.key;
	}
	
	class Request implements Comparable<Request>
	{
		final double key, rate, time;
		
		Request (double key, double rate, double time)
		{
			this.key = key;
			this.rate = rate;
			this.time = time;
		}
		
		// Must be consistent with compareTo()
		public boolean equals (Request r)
		{
			return key == r.key && rate == r.rate && time == r.time;
		}
		
		// Must be consistent with equals()
		public int compareTo (Request r)
		{
			if (time < r.time) return -1;
			if (time > r.time) return 1;
			// Arbitrarily break ties using rate, then key
			if (rate < r.rate) return -1;
			if (rate > r.rate) return 1;
			if (key < r.key) return -1;
			if (key > r.key) return 1;
			return 0;
		}
	}
}
