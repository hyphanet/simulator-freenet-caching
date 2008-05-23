// This software has been placed in the public domain by its author

class ZipfRequestGenerator extends RequestGenerator
{
	double requestRate()
	{
		return 1.0 / Math.random(); // Zipf distribution
	}
}
