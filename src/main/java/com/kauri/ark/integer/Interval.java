/*
 * This file is part of the ark package.
 *
 * Copyright (c) 2014 Eric Fritz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.kauri.ark.integer;

/**
 * Interval
 *
 * @author Eric Fritz
 */
public class Interval
{
	public static final int MIN_VALUE = -1000000;
	public static final int MAX_VALUE = +1000000;

	private int lowerBound;
	private int upperBound;

	public Interval(int lowerBound, int upperBound) {
		if (lowerBound < MIN_VALUE || upperBound > MAX_VALUE) {
			throw new RuntimeException("Interval exceeds minimum or maximum bounds.");
		}

		if (lowerBound > upperBound) {
			throw new RuntimeException("Interval is empty.");
		}

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public int getLower() {
		return lowerBound;
	}

	public int getUpper() {
		return upperBound;
	}

	public Interval intersection(Interval other) {
		int lower = Math.max(this.getLower(), other.getLower());
		int upper = Math.min(this.getUpper(), other.getUpper());

		if (lower <= upper) {
			return new Interval(lower, upper);
		}

		return null;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof Interval)) {
			return false;
		}

		Interval i = (Interval) o;
		return i.lowerBound == lowerBound && i.upperBound == upperBound;
	}

	public String toString() {
		return String.format("[%d, %d]", lowerBound, upperBound);
	}
}
