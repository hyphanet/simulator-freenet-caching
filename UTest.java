// This software has been placed in the public domain by its author

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

class UTest
{
	static final double zCritical = 2.576; // P = 0.01, two-tailed
	// static final double zCritical = 1.96; // P = 0.05, two-tailed

	
	static final int SMALLER = -1, INCONCLUSIVE = 0, LARGER = 1;
	
	static void die (String message)
	{
		System.err.println (message);
		System.exit (1);
	}
	
	static ArrayList <Double> readFile (String filename)
	{
		ArrayList <Double> arr = new ArrayList <Double> ();
		try {
			BufferedReader in;
			in = new BufferedReader (new FileReader (filename));
			while (true) {
				String s = in.readLine();
				if (s == null) break;
				arr.add (new Double (s));
			}
			in.close();
		}
		catch (FileNotFoundException fnf) {
			die (filename + " not found");
		}
		catch (IOException io) {
			die ("Error reading from " + filename);
		}
		catch (NumberFormatException nf) {
			die ("Invalid data in " + filename);
		}
		return arr;
	}
	
	// The method used here is explained at
	// http://faculty.vassar.edu/lowry/ch11a.html
	static int test (String f1, String f2)
	{
		ArrayList <Double> a = readFile (f1);
		ArrayList <Double> b = readFile (f2);
		int nA = a.size(), nB = b.size();		
		if (nA < 5 || nB < 5) {
			System.err.println ("Too few samples for U test\n");
			return INCONCLUSIVE;
		}
		
		/*
		// Calculate the total rank of each set (old method, O(n^2))
		ArrayList <Double> merged = new ArrayList <Double> (nA + nB);
		merged.addAll (a);
		merged.addAll (b);
		Collections.sort (merged);
		double tA = 0.0, tB = 0.0;				
		for (double x : a) {
			int lessThan = 0, equalTo = 0;
			for (double y : merged) {
				if (y < x) lessThan++;
				else if (y == x) equalTo++;
				else break;
			}
			tA += lessThan + (equalTo + 1) / 2.0;
		}
		for (double x : b) {
			int lessThan = 0, equalTo = 0;
			for (double y : merged) {
				if (y < x) lessThan++;
				else if (y == x) equalTo++;
				else break;
			}
			tB += lessThan + (equalTo + 1) / 2.0;
		}
		System.out.println ("Old method: " + tA + " " + tB);
		*/
		
		// Calculate the total rank of each set (new method, O(n log n))
		double tA = 0.0, tB = 0.0;
		Collections.sort (a);
		Collections.sort (b);
		int iA = 0, iB = 0, lessThan = 0;
		while (iA < nA || iB < nB) {
			int ties = 0, tieRanks = 0;
			double currA, currB;
			if (iA < nA) currA = a.get (iA);
			else currA = Double.POSITIVE_INFINITY;
			if (iB < nB) currB = b.get (iB);
			else currB = Double.POSITIVE_INFINITY;
			// If there are no more bs or next a is less than next b
			if (currA < currB) {
				// Count ties in a
				while (iA + ties < nA &&
					a.get (iA + ties) == currA) {
					ties++;
					tieRanks += ties;
				}
				// Increase a's total rank and update the index
				tA += tieRanks + ties * lessThan;
				iA += ties;
			}
			// Same the other way round
			else if (currA > currB) {
				// Count ties in b
				while (iB + ties < nB &&
					b.get (iB + ties) == currB) {
					ties++;
					tieRanks += ties;
				}
				// Increase b's total rank and update the index
				tB += tieRanks + ties * lessThan;
				iB += ties;
			}
			// Next a equal to next b: this is the tricky one
			else {
				int tiesA = 0, tiesB = 0;
				// Count ties in a
				while (iA + tiesA < nA &&
					a.get (iA + tiesA) == currA) {
					tiesA++;
					ties++;
					tieRanks += ties;
				}
				// Count ties in b
				while (iB + tiesB < nB &&
					b.get (iB + tiesB) == currB) {
					tiesB++;
					ties++;
					tieRanks += ties;
				}
				double mean = tieRanks / (double) ties;
				// Increase a's total rank and update the index
				tA += tiesA * (mean + lessThan);
				iA += tiesA;
				// Increase b's total rank and update the index
				tB += tiesB * (mean + lessThan);
				iB += tiesB;
			}
			lessThan += ties;
		}
		
		// The standard deviation of both total ranks is the same
		double sigma = Math.sqrt (nA * nB * (nA + nB + 1.0) / 12.0);
		// Means of the distributions of the total ranks
		double muA = nA * (nA + nB + 1.0) / 2.0;
		double muB = nB * (nA + nB + 1.0) / 2.0;
		// Z scores
		double zA, zB;
		if (tA > muA) zA = (tA - muA - 0.5) / sigma;
		else zA = (tA - muA + 0.5) / sigma;
		if (tB > muB) zB = (tB - muB - 0.5) / sigma;
		else zB = (tB - muB + 0.5) / sigma;
		
		if (zA > zCritical) return LARGER;
		else if (zB > zCritical) return SMALLER;
		else return INCONCLUSIVE;
	}
	
	public static void main (String[] args)
	{
		if (args.length != 2) die ("usage: UTest <file1> <file2>");
		switch (test (args[0], args[1])) {
			case SMALLER:
			System.out.println (args[0] + " is smaller");
			break;
			
			case INCONCLUSIVE:
			System.out.println ("No significant difference");
			break;
			
			case LARGER:
			System.out.println (args[0] + " is larger");
			break;
		}
	}
}
