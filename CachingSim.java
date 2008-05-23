// This software has been placed in the public domain by its author

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;

class CachingSim
{
	public static int NODES = 1000, DEGREE = 20, KEYS = 200000;
	
	ArrayList<Node> nodes;
	RequestGenerator requests;
	
	CachingSim()
	{
		nodes = new ArrayList<Node> (NODES);
		for (int i = 0; i < NODES; i++) nodes.add (Node.create());
		makeKleinbergNetwork();
		ArrayList<Double> keys = new ArrayList<Double> (KEYS);
		for (int i = 0; i < KEYS; i++) keys.add (Math.random());
		requests = RequestGenerator.create();
		requests.init (keys);
		storeKeys (keys);
	}
	
	void run()
	{
		double success = 0.0, depth = 0.0, cost = 0.0;
		// Request each key 100 times
		for (int i = 0; i < KEYS * 100; i++) {
			// Reset the counters halfway through the simulation
			if (i == KEYS * 50) success = depth = cost = 0.0;
			int random = (int) (Math.random() * nodes.size());
			Node node = nodes.get (random);
			double key = requests.generateRequest();
			HashSet<Node> visited = new HashSet<Node>();
			int d = node.request (key, 0, visited);
			if (d != -1) {
				success++; // Success rate should be close to 1
				depth += d; // Depth of successful searches
				cost += visited.size() - 1; // Bandwidth cost
			}
		}
		System.out.println (
			success / (KEYS * 50) + " " +
			depth / success + " " +
			cost / success
		);
	}
	
	void makeKleinbergNetwork()
	{
		for (Node a : nodes) {
			// Normalise the probabilities
			double norm = 0.0;
			for (Node b : nodes) {
				if (a.loc == b.loc) continue;
				norm += 1.0 / distance (a.loc, b.loc);
			}
			// Create DEGREE/2 outgoing connections
			for (Node b : nodes) {
				if (a.loc == b.loc) continue;
				double p = 1.0 / distance (a.loc, b.loc) / norm;
				for (int i = 0; i < DEGREE / 2; i++) {
					if (Math.random() < p) {
						a.neighbours.add (b);
						b.neighbours.add (a);
						break;
					}
				}
			}
		}
	}
	
	static double distance (double x, double y)
	{
		if (x > y) return Math.min (x - y, y - x + 1.0);
		else return Math.min (y - x, x - y + 1.0);
	}
	
	// Store each key permanently on the node nearest its location
	void storeKeys (ArrayList<Double> keys)
	{
		for (double key : keys) {
			double bestDistance = Double.POSITIVE_INFINITY;
			Node bestNode = null;
			for (Node node : nodes) {
				double distance = distance (node.loc, key);
				if (distance < bestDistance) {
					bestDistance = distance;
					bestNode = node;
				}
			}
			bestNode.store.add (key);
		}
	}
	
	public static void main (String[] args)
	{
		// Command line args are used to set static values by reflection
		try {
			for (String arg : args) {
				// Arguments are in the form class.field=value
				String[] cfv = arg.split ("=");
				String[] cf = cfv[0].split ("\\.");
				Class c = Class.forName (cf[0]);
				Field f = c.getField (cf[1]);
				String v = cfv[1];
				// Determine the type and parse the value
				Class type = f.getType();
				if (type == Boolean.TYPE) {
					boolean b = Boolean.parseBoolean (v);
					f.setBoolean (null, b);
				}
				else if (type == Double.TYPE) {
					double d = Double.parseDouble (v);
					f.setDouble (null, d);
				}
				else if (type == Integer.TYPE) {
					int i = Integer.parseInt (v);
					f.setInt (null, i);
				}
				else if (type == Class.class) {
					ClassLoader cl
					= ClassLoader.getSystemClassLoader();
					f.set (null, cl.loadClass (v));
				}
			}
		}
		catch (Exception e) {
			System.err.println (e);
			System.exit (1);
		}
		// Print the arguments so we can tell the output files apart
		System.out.print ("# args:");
		for (String arg : args) System.out.print (" " + arg);
		System.out.println();
		// Run the simulation
		new CachingSim().run();
	}
}
