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
 * IntegerSignConstraint
 *
 * @author Eric Fritz
 */
public class IntegerSignConstraint implements Constraint<IntegerDomain>
{
	private Variable<IntegerDomain> var1;
	private Variable<IntegerDomain> var2;

	public IntegerSignConstraint(Variable<IntegerDomain> var1, Variable<IntegerDomain> var2) {
		this.var1 = var1;
		this.var2 = var2;
	}

	@Override
	public boolean update(Variable<IntegerDomain> variable) {
		Variable<IntegerDomain> other = variable == var1 ? var2 : var1;

		int lower = Interval.MIN_VALUE;
		int upper = Interval.MAX_VALUE;

		if (variable == var1) {
			// var1 = sign(var2)
			// var2 has changed

			// TODO - can trim anyway

			if (other.getDomain().isUnique()) {
				int sign = other.getDomain().getUniqueValue();

				if (sign == -1) {
					// negative
					upper = -1;
				} else if (sign == 0) {
					// zero
					lower = 0;
					upper = 0;
				} else if (sign == 1) {
					// positive
					lower = 1;
				} else {
					// did not narrow domain
					return true;
				}
			}
		} else {
			// var1 = sign(var2)
			// var1 has changed

			lower = -1;
			upper = +1;

			if (other.getDomain().getMaximum() < 0) {
				// all negative
				upper = -1;
			} else {
				if (other.getDomain().getMinimum() > 0) {
					// all positive
					lower = 1;
				} else {
					// TODO - try to rule out zero?
				}
			}
		}

		return variable.trySetValue(variable.getDomain().retain(new Interval(lower, upper)));
	}
}
