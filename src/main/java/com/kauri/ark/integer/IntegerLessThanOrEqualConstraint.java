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
 * IntervalLessThanOrEqualConstraint
 *
 * @author Eric Fritz
 */
public class IntegerLessThanOrEqualConstraint implements Constraint<IntegerVariable>
{
	private IntegerVariable var1;
	private IntegerVariable var2;

	public IntegerLessThanOrEqualConstraint(IntegerVariable var1, IntegerVariable var2) {
		this.var1 = var1;
		this.var2 = var2;
	}

	@Override
	public boolean update(IntegerVariable variable) {
		IntegerVariable other = variable == var1 ? var2 : var1;

		IntervalSet set = new IntervalSet(variable.getCurrentAllowableValues());

		if (variable == var1) {
			// remove everything greater than the largest integer in other
			set.remove(new Interval(other.getCurrentAllowableValues().getMaximum() + 1, Integer.MAX_VALUE));
		} else {
			// remove everything smaller than the smallest integer in other
			set.remove(new Interval(Integer.MIN_VALUE, other.getCurrentAllowableValues().getMinimum() - 1));
		}

		return variable.trySetValue(set);
	}
}
