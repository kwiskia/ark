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

package com.kauri.ark;

/**
 * SumConstraint
 *
 * @author Eric Fritz
 */
public class SumConstraint extends Constraint<IntegerVariable>
{
	private IntegerVariable a;
	private IntegerVariable b;
	private IntegerVariable c;

	public SumConstraint(Solver solver, IntegerVariable a, IntegerVariable b, IntegerVariable c) {
		super(solver, a, b, c);

		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public boolean update(IntegerVariable variable) {
		int aLower = a.allowableValues.getLowerBound();
		int aUpper = a.allowableValues.getUpperBound();
		int bLower = b.allowableValues.getLowerBound();
		int bUpper = b.allowableValues.getUpperBound();
		int cLower = c.allowableValues.getLowerBound();
		int cUpper = c.allowableValues.getUpperBound();

		int lower;
		int upper;

		if (variable == a) {
			// c - b
			lower = cLower - bUpper;
			upper = cUpper - bLower;
		} else if (variable == b) {
			// c - a
			lower = cLower - aUpper;
			upper = cUpper - aLower;
		} else if (variable == c) {
			// a + b
			lower = aLower + bLower;
			upper = aUpper + bUpper;
		} else {
			throw new RuntimeException("Unreachable.");
		}

		lower = Math.max(variable.allowableValues.getLowerBound(), lower);
		upper = Math.min(variable.allowableValues.getUpperBound(), upper);

		return variable.trySetValue(new Interval(lower, upper));
	}
}
