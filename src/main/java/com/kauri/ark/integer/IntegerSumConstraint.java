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
import com.kauri.ark.Variable;

/**
 * SumConstraint
 *
 * @author Eric Fritz
 */
public class IntegerSumConstraint implements Constraint<Variable<IntegerDomain>>
{
	private Variable<IntegerDomain> a;
	private Variable<IntegerDomain> b;
	private Variable<IntegerDomain> c;

	public IntegerSumConstraint(Variable<IntegerDomain> a, Variable<IntegerDomain> b, Variable<IntegerDomain> c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public boolean update(Variable<IntegerDomain> variable) {
		int aLower = a.getDomain().getMinimum();
		int aUpper = a.getDomain().getMaximum();
		int bLower = b.getDomain().getMinimum();
		int bUpper = b.getDomain().getMaximum();
		int cLower = c.getDomain().getMinimum();
		int cUpper = c.getDomain().getMaximum();

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

		lower = Math.max(variable.getDomain().getMinimum(), lower);
		upper = Math.min(variable.getDomain().getMaximum(), upper);

		return variable.trySetValue(variable.getDomain().add(new Interval(lower, upper)));
	}
}
