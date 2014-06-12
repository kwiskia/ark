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
 * A constraint which forces an integer variable to be the absolute value of another integer variable.
 *
 * @author Eric Fritz
 */
public class IntegerAbsoluteValueConstraint implements Constraint<IntegerDomain>
{
	/**
	 * The unconstrainted variable.
	 */
	private Variable<IntegerDomain> var1;

	/**
	 * The absolute value variable.
	 */
	private Variable<IntegerDomain> var2;

	/**
	 * Creates a new IntegerAbsoluteValueConstraint.
	 *
	 * @param var1 The unconstrainted variable.
	 * @param var2 The absolute value variable.
	 */
	public IntegerAbsoluteValueConstraint(Variable<IntegerDomain> var1, Variable<IntegerDomain> var2) {
		this.var1 = var1;
		this.var2 = var2;
	}

	@Override
	public boolean narrow(Variable<IntegerDomain> variable) {
		// Narrow the domain of the argument variable to retain only consistent (valid) values.
		//   1) when updating var1, non-negative values in var2 and negated non-negative values in var2 are valid, and
		//   2) when updating var2, non-negative values in var1 and negated     negative values in var1 are valid.

		IntegerDomain domain1 = variable == var1 ? var1.getDomain() : var2.getDomain();
		IntegerDomain domain2 = variable == var1 ? var2.getDomain() : var1.getDomain();

		IntegerDomain pos = domain2.retain(new Interval(+0, Interval.MAX_VALUE));
		IntegerDomain neg = domain2.retain(new Interval(Interval.MIN_VALUE, -1));

		if (variable == var1) {
			return variable.trySetValue(domain1.retainAll(pos.concat(pos.negate())));
		} else {
			return variable.trySetValue(domain1.retainAll(pos.concat(neg.negate())));
		}
	}
}
