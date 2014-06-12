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
 * A constraint which forces an integer variable to have the minimum value of two other integer variables.
 *
 * @author Eric Fritz
 */
public class IntegerMinConstraint implements Constraint<IntegerDomain>
{
	/**
	 * The minimum variable.
	 */
	private Variable<IntegerDomain> min;

	/**
	 * The variables whose minimum value constrains <tt>min</tt>.
	 */
	private Variable<IntegerDomain>[] variables;

	/**
	 * Creates a new IntegerMinConstraint.
	 *
	 * @param min       The minimum variable.
	 * @param variables The variables whose minimum value constrains <tt>min</tt>.
	 */
	public IntegerMinConstraint(Variable<IntegerDomain> min, Variable<IntegerDomain>... variables) {
		this.min = min;
		this.variables = variables;
	}

	@Override
	public boolean narrow(Variable<IntegerDomain> variable) {
		// If we are updating the min variable, cap it between the minimum lower bound and the maximum upper bound
		// of all the variables. If we are updating any other variable, ensure it doesn't exceed min's lower bound.

		if (variable == min) {
			int lower = Interval.MAX_VALUE;
			int upper = Interval.MIN_VALUE;

			for (Variable<IntegerDomain> v : variables) {
				lower = Math.min(lower, v.getDomain().getMinimum());
				upper = Math.max(upper, v.getDomain().getMaximum());
			}

			return variable.trySetValue(variable.getDomain().retain(new Interval(lower, upper)));
		} else {
			int lower = min.getDomain().getMinimum();
			int upper = Interval.MAX_VALUE;

			return variable.trySetValue(variable.getDomain().retain(new Interval(lower, upper)));
		}
	}
}
