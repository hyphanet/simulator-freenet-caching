// This software has been placed in the public domain by its author

class UniformRequestGenerator extends RequestGenerator
{
	double requestRate()
	{
		return 1.0; // All keys are requested at the same rate
	}
}
