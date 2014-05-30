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
 * IntervalEqualityConstraint
 *
 * @author Eric Fritz
 */
public class IntegerEqualityConstraint implements Constraint<IntegerVariable>
{
	private IntegerVariable[] variables;

	public IntegerEqualityConstraint(IntegerVariable... variables) {
		this.variables = variables;
	}

	@Override
	public boolean update(IntegerVariable variable) {

		int lower = variable.getCurrentAllowableValues().getMinimum();
		int upper = variable.getCurrentAllowableValues().getMaximum();

		for (IntegerVariable v : variables) {
			if (v != variable) {
				lower = Math.max(lower, v.getCurrentAllowableValues().getMinimum());
				upper = Math.min(upper, v.getCurrentAllowableValues().getMaximum());
			}
		}

		IntervalSet set = new IntervalSet();
		set.add(new Interval(lower, upper));

		return variable.trySetValue(set);
	}
}
