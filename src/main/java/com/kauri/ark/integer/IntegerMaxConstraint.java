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
 * A constraint which forces an integer variable to have the maximum value of two other integer variables.
 *
 * @author Eric Fritz
 */
public class IntegerMaxConstraint implements Constraint<IntegerDomain>
{
	/**
	 * The maximum variable.
	 */
	private Variable<IntegerDomain> max;

	/**
	 * The variables whose maximum value constrains <tt>max</tt>.
	 */
	private Variable<IntegerDomain>[] variables;

	/**
	 * Creates a new IntegerMaxConstraint.
	 *
	 * @param max       The maximum variable.
	 * @param variables The variables whose maximum value constrains <tt>max</tt>.
	 */
	public IntegerMaxConstraint(Variable<IntegerDomain> max, Variable<IntegerDomain>... variables) {
		this.max = max;
		this.variables = variables;
	}

	@Override
	public boolean narrow(Variable<IntegerDomain> variable) {
		// If we are updating the max variable, cap it between the minimum lower bound and the maximum upper bound
		// of all the variables. If we are updating any other variable, ensure it doesn't exceed max's upper bound.

		if (variable == max) {
			int lower = Interval.MAX_VALUE;
			int upper = Interval.MIN_VALUE;

			for (Variable<IntegerDomain> v : variables) {
				lower = Math.min(lower, v.getDomain().getMinimum());
				upper = Math.max(upper, v.getDomain().getMaximum());
			}

			return variable.trySetValue(variable.getDomain().retain(new Interval(lower, upper)));
		} else {
			int lower = Interval.MIN_VALUE;
			int upper = max.getDomain().getMaximum();

			return variable.trySetValue(variable.getDomain().retain(new Interval(lower, upper)));
		}
	}
}
