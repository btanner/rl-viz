package rlVizLib.utilities;


public class LogCompressor {
	
	public static int putSomeAintoB(int A, int B, int amount, int offset) throws Exception {
		if (A < 0)
			throw new Exception();
		if (B < 0)
			throw new Exception();
		if (amount < 0)
			throw new Exception();
		if (offset < 0)
			throw new Exception();
		if (amount + offset >= 32)
			throw new Exception();

		if (amount == 0)
			return B;
		// mask off higher values from A
		int mask = (1 << amount) - 1;
		A = A & mask;
		// shift A up to offset
		A = A << offset;
		mask = mask << offset;
		// zero out this section in B
		B = B & (~mask);
		// bitwise or shifted A with cleared B
		B = B | A;
		return B;
	}
	
	public static int getSomeAfromB(int A, int B, int amount, int offset) throws Exception {
		if (A < 0)
			throw new Exception();
		if (B < 0)
			throw new Exception();
		if (amount < 0)
			throw new Exception();
		if (offset < 0)
			throw new Exception();
		if (amount + offset >= 32)
			throw new Exception();

		if (amount == 0)
			return A;
		// shift down B
		B = B >> offset;
		// mask off higher values from B
		int mask = (1 << amount) - 1;
		B = B & mask;
		// zero out section in A
		A = A & (~mask);
		// or B with A
		A = A | B;
		return A;
	}

}