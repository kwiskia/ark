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
import java.util.ArrayList;
import java.util.List;

/**
 * A constraint which forces an integer variable to have the sign of another integer variable. The sign value is
 * one of <tt>-1</tt>, <tt>0</tt>, or <tt>1</tt> for negative, zero, and positive values respectively.
 *
 * @author Eric Fritz
 */
public class IntegerSignConstraint implements Constraint<IntegerDomain>
{
	/**
	 * The unconstrained variable.
	 */
	private Variable<IntegerDomain> var1;

	/**
	 * The sign variable.
	 */
	private Variable<IntegerDomain> var2;

	/**
	 * Creates a new IntegerSignConstraint.
	 *
	 * @param var1 The unconstrained variable.
	 * @param var2 The sign variable.
	 */
	public IntegerSignConstraint(Variable<IntegerDomain> var1, Variable<IntegerDomain> var2) {
		this.var1 = var1;
		this.var2 = var2;
	}

	@Override
	public boolean narrow(Variable<IntegerDomain> variable) {
		IntegerDomain domain1 = variable == var1 ? var1.getDomain() : var2.getDomain();
		IntegerDomain domain2 = variable == var1 ? var2.getDomain() : var1.getDomain();

		if (variable == var1) {
			// Narrow the domain of the argument variable to include only the elements consistent with the current sign.
			//   1) If `-1' is not present in the sign domain, then remove all negative numbers from var2's domain, and
			//   2) If `+1' is not present in the sign domain, then remove all positive numbers from var2's domain, and
			//   3) If `+0' is not present in the sign domain, then remove `0' from var2's domain.

			int lower = !domain2.contains(-1) ? 0 : Interval.MIN_VALUE;
			int upper = !domain2.contains(+1) ? 0 : Interval.MAX_VALUE;

			if (!domain2.contains(0)) {
				List<Interval> intervals = new ArrayList<>();
				if (lower != 0) intervals.add(new Interval(lower, -1));
				if (upper != 0) intervals.add(new Interval(+1, upper));

				return variable.trySetValue(domain1.retainAll(new IntegerDomain().concat(intervals)));
			}

			return variable.trySetValue(domain1.retain(new Interval(lower, upper)));
		} else {
			// Narrow the domain of the argument variable to include only signs consistent with var2's domain.
			//   1) If var2's domain is disjoint from [-Infinity, -1], then remove -1 from the sign domain, and
			//   2) If var2's domain is disjoint from [+1, +Infinity], then remove +1 from the sign domain, and
			//   3) If var2's domain does not contain `+0', then remove `0' from the sign domain.

			int lower = domain2.getMinimum() < 0 ? -1 : 0;
			int upper = domain2.getMaximum() > 0 ? +1 : 0;

			if (!domain2.contains(0)) {
				if (lower == -1 && upper == +1) {
					List<Interval> intervals = new ArrayList<>();
					intervals.add(new Interval(-1, -1));
					intervals.add(new Interval(+1, +1));

					return variable.trySetValue(domain1.retainAll(new IntegerDomain().concat(intervals)));
				}

				if (lower == -1) upper = -1;
				if (upper == +1) lower = +1;
			}

			return variable.trySetValue(domain1.retain(new Interval(lower, upper)));
		}
	}
}
