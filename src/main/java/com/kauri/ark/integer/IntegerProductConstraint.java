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

import com.kauri.ark.Constraint;

/**
 * ProductConstraint
 *
 * @author Eric Fritz
 */
public class IntegerProductConstraint implements Constraint<IntegerVariable>
{
	private IntegerVariable a;
	private IntegerVariable b;
	private IntegerVariable c;

	public IntegerProductConstraint(IntegerVariable a, IntegerVariable b, IntegerVariable c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public boolean update(IntegerVariable variable) {
		int aLower = a.getCurrentAllowableValues().getMinimum();
		int aUpper = a.getCurrentAllowableValues().getMaximum();
		int bLower = b.getCurrentAllowableValues().getMinimum();
		int bUpper = b.getCurrentAllowableValues().getMaximum();
		int cLower = c.getCurrentAllowableValues().getMinimum();
		int cUpper = c.getCurrentAllowableValues().getMaximum();

		int lower;
		int upper;

		if (variable == a) {
			// c / b
			if (bLower == 0 || bUpper == 0) {
				if (a.isUnique() && c.isUnique()) {
					return c.getCurrentAllowableValues().getMinimum() == 0;
				}

				return true;
			}

			lower = min(cLower / bLower, cLower / bUpper, cUpper / bLower, cUpper / bUpper);
			upper = max(cLower / bLower, cLower / bUpper, cUpper / bLower, cUpper / bUpper);
		} else if (variable == b) {
			// c / a
			if (aLower == 0 || aUpper == 0) {
				if (b.isUnique() && c.isUnique()) {
					return c.getCurrentAllowableValues().getMinimum() == 0;
				}

				return true;
			}

			lower = min(cLower / aLower, cLower / aUpper, cUpper / aLower, cUpper / aUpper);
			upper = max(cLower / aLower, cLower / aUpper, cUpper / aLower, cUpper / aUpper);
		} else if (variable == c) {
			// a * b
			lower = min(aLower * bLower, aLower * bUpper, aUpper * bLower, aUpper * bUpper);
			upper = max(aLower * bLower, aLower * bUpper, aUpper * bLower, aUpper * bUpper);
		} else {
			throw new RuntimeException("Unreachable.");
		}

		lower = Math.max(variable.getCurrentAllowableValues().getMinimum(), lower);
		upper = Math.min(variable.getCurrentAllowableValues().getMaximum(), upper);

		IntervalSet set = new IntervalSet();
		set.add(new Interval(lower, upper));

		return variable.trySetValue(set);
	}

	private int min(int a, int b, int c, int d) {
		return Math.min(a, Math.min(b, Math.min(c, d)));
	}

	private int max(int a, int b, int c, int d) {
		return Math.max(a, Math.max(b, Math.max(c, d)));
	}
}
