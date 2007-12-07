/* RL-VizLib, a library for C++ and Java for adding advanced visualization and dynamic capabilities to RL-Glue.
 * Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package rlVizLib.utilities;

import java.io.File;
import java.util.StringTokenizer;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.hasVersionDetails;
import rlglue.types.Observation;

/**
 * The Utility Shop is a useful dump for all sorts of odds and ends that come in
 * handy. Some day it should probably be deprecated and distributed.
 * 
 * @author Brian Tanner
 * @author Mark Lee
 * 
 */
/**
 * @author btanner
 * 
 */
public class UtilityShop {

	public static double normalizeValue(double theValue, double minPossible,
			double maxPossible) {
		return (theValue - minPossible) / (maxPossible - minPossible);
	}

	public static Observation cloneObservation(Observation theObs) {

		Observation newObs = new Observation(theObs.intArray.length,
				theObs.doubleArray.length);
		for (int i = 0; i < theObs.intArray.length; i++) {
			newObs.intArray[i] = theObs.intArray[i];
		}
		for (int i = 0; i < theObs.doubleArray.length; i++) {
			newObs.doubleArray[i] = theObs.doubleArray[i];
		}
		return newObs;
	}

	public static StringBuffer serializeObservation(
			StringBuffer theRequestBuffer, Observation theObs) {
		theRequestBuffer.append(theObs.intArray.length);
		theRequestBuffer.append("_");
		for (int i = 0; i < theObs.intArray.length; i++) {
			theRequestBuffer.append(theObs.intArray[i]);
			theRequestBuffer.append("_");
		}
		theRequestBuffer.append(theObs.doubleArray.length);
		theRequestBuffer.append("_");
		for (int i = 0; i < theObs.doubleArray.length; i++) {
			theRequestBuffer.append(theObs.doubleArray[i]);
			theRequestBuffer.append("_");
		}
		return theRequestBuffer;
	}

	public static Observation buildObservationFromString(String thisObsString) {
		StringTokenizer obsTokenizer = new StringTokenizer(thisObsString, "_");

		String intCountToken = obsTokenizer.nextToken();
		int theIntCount = Integer.parseInt(intCountToken);
		int[] theInts = new int[theIntCount];

		for (int i = 0; i < theInts.length; i++) {
			theInts[i] = Integer.parseInt(obsTokenizer.nextToken());
		}

		String doubleCountToken = obsTokenizer.nextToken();
		int theDoubleCount = Integer.parseInt(doubleCountToken);
		double[] theDoubles = new double[theDoubleCount];

		for (int i = 0; i < theDoubles.length; i++) {
			theDoubles[i] = Double.parseDouble(obsTokenizer.nextToken());
		}

		Observation theObs = new Observation(theIntCount, theDoubleCount);
		theObs.intArray = theInts;
		theObs.doubleArray = theDoubles;

		return theObs;
	}

	public static String serializeObservation(Observation o) {
		StringBuffer b = new StringBuffer();
		return serializeObservation(b, o).toString();
	}

	/**
	 * Takes the high half of the bits out of a long and tells you what int they
	 * are
	 * 
	 * @param thisLong
	 * @return
	 * @author Brian Tanner
	 */
	public static final int LongHighBitsToInt(Long thisLong) {
		int b = (int) (thisLong >>> 32);
		return b;
	}

	/**
	 * Takes the low half of the bits out of a long and tells you want int they
	 * are
	 * 
	 * @param thisLong
	 * @return
	 * @author Brian Tanner
	 */
	public static final int LongLowBitsToInt(Long thisLong) {
		int a = (int) (thisLong & 0x00000000FFFFFFFF);

		return a;
	}

	/**
	 * Takes two int and smooshes them into a long.
	 * 
	 * @param highBits
	 * @param lowBits
	 * @return
	 * @author Brian Tanner
	 */
	public static final long intsToLong(int highBits, int lowBits) {
		long newLong = (4294967295L & (long) lowBits) | (long) highBits << 32;
		return newLong;
	}

	/**
	 * 
	 * Returns the path the the RLViz Libraries as has been set by the
	 * RLVIZ_LIB_PATH System property or by hopings its at ../../libraries
	 * 
	 * @return
	 * @author Brian Tanner
	 */
	public final static String getLibraryPath() {
		// Some more dynamic loading goodness
		String libraryPath = System.getProperty("RLVIZ_LIB_PATH");
		if (libraryPath == null) {
			String curDir = System.getProperty("user.dir");
			File thisDirectoryFile = new File(curDir);
			String mainLibraryDir = thisDirectoryFile.getParent();
			File parentDirectoryFile = new File(mainLibraryDir);
			String workSpaceDirString = parentDirectoryFile.getParent();
			libraryPath = workSpaceDirString + "/libraries";
		}
		return libraryPath;
	}

	/**
	 * Given a {@link hasVersionDetails} provider, fills fields into the ParameterHolder formatted
	 * so that they show up nicely in the application that might want to display them 
	 * @param P
	 * @param provider
     * @author Brian Tanner
	 */
	public final static void setVersionDetails(ParameterHolder P,
			hasVersionDetails provider) {
		if (P != null) {
			P.addStringParam("###name", provider.getName());
			P.addStringParam("###shortname", provider.getShortName());
			P.addStringParam("###url", provider.getInfoUrl());
			P.addStringParam("###authors", provider.getAuthors());
			P.addStringParam("###description", provider.getDescription());
		}
	}

	
	/**
	 * Isolates some of the bits from int A and uses bit manipulation to insert them at an offset in B
	 * <p>
	 * Need an example here.
	 * @param A Source of the bits to be copy (they should be stored in the first amount bits)
	 * @param B	Destination of the bits 
	 * @param amount Number of the bits to copy
	 * @param offset Offset to put the bits at
	 * @return B with the bits transplanted
	 * @throws Exception 
	 * @author Mark Lee
	 * @author Brian Tanner
	 */
	public static int putSomeBitsFromIntIntoInt(int A, int B, int amount,
			int offset) throws Exception {
		if (A < 0)
			throw new IllegalArgumentException("A should be non-negative");
		if (B < 0)
			throw new IllegalArgumentException("B should be non-negative");
		if (amount < 0)
			throw new IllegalArgumentException("amount should be non-negative");
		if (offset < 0)
			throw new IllegalArgumentException("offset should be non-negative");
		if (amount + offset >= 32)
			throw new IllegalArgumentException("amount + offset should not be larger than 31");

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

	/**
	 * Isolate some of the bits from B and return them as an int
	 * @param B  Int with bits stuffed into it that we want to extract
	 * @param amount The number of bits to extract
	 * @param offset Where to start extracting
	 * @return Int made from the extracted bits
	 * @throws Exception
	 * @author Mark Lee
	 * @author Brian Tanner
	 */
	public static int extractSomeBitsAsIntFromInt(int B, int amount,
			int offset){
		int A=0;
		if (B < 0)
			throw new IllegalArgumentException("B should be non-negative");
		if (amount < 0)
			throw new IllegalArgumentException("amount should be non-negative");
		if (offset < 0)
			throw new IllegalArgumentException("offset should be non-negative");
		if (amount + offset >= 32)
			throw new IllegalArgumentException("amount + offset should not be larger than 31");
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