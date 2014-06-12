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
 * A constraint which forces an integer variable to be less than or equal to another integer variable.
 *
 * @author Eric Fritz
 */
public class IntegerLessThanOrEqualConstraint implements Constraint<IntegerDomain>
{
	/**
	 * The smaller variable.
	 */
	private Variable<IntegerDomain> var1;

	/**
	 * The larger variable.
	 */
	private Variable<IntegerDomain> var2;

	/**
	 * Creates a new IntegerLessThanOrEqualConstraint.
	 *
	 * @param var1 The smaller variable.
	 * @param var2 The larger variable.
	 */
	public IntegerLessThanOrEqualConstraint(Variable<IntegerDomain> var1, Variable<IntegerDomain> var2) {
		this.var1 = var1;
		this.var2 = var2;
	}

	@Override
	public boolean narrow(Variable<IntegerDomain> variable) {
		// Narrow the domain of the argument variable to retain only consistent (valid) values:
		//   1) when updating var1 variable, [..., max(var2)] is are valid, and
		//   2) when updating var2 variable, [min(var1), ...] is are valid.

		IntegerDomain domain1 = variable == var1 ? var1.getDomain() : var2.getDomain();
		IntegerDomain domain2 = variable == var1 ? var2.getDomain() : var1.getDomain();

		int lower = variable == var2 ? domain2.getMinimum() : Interval.MIN_VALUE;
		int upper = variable == var1 ? domain2.getMaximum() : Interval.MAX_VALUE;

		return variable.trySetValue(domain1.retain(new Interval(lower, upper)));
	}
}
