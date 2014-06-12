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
 * A constraint which forces an integer variable to be the sum of two other integer variables.
 *
 * @author Eric Fritz
 */
public class IntegerSumConstraint implements Constraint<IntegerDomain>
{
	/**
	 * The augend variable.
	 */
	private Variable<IntegerDomain> a;

	/**
	 * The addend variable.
	 */
	private Variable<IntegerDomain> b;

	/**
	 * The sum variable.
	 */
	private Variable<IntegerDomain> c;

	/**
	 * Creates a new IntegerSumConstraint.
	 *
	 * @param a The augend variable.
	 * @param b The addend variable.
	 * @param c The sum variable.
	 */
	public IntegerSumConstraint(Variable<IntegerDomain> a, Variable<IntegerDomain> b, Variable<IntegerDomain> c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public boolean narrow(Variable<IntegerDomain> variable) {
		// Narrow the domain of the argument variable to include (at most) the intervals satisfying:
		//   [when variable == a]: ci - bj for each interval ci in c and bj in b,
		//   [when variable == b]: ci - aj for each interval ci in c and aj in a,
		//   [when variable == c]: ai + bj for each interval ai in a and bj in b.

		List<Interval> intervals = new ArrayList<>();

		if (variable == a) {
			for (Interval ci : c.getDomain()) {
				for (Interval bj : b.getDomain()) {
					addDifference(intervals, ci, bj);
				}
			}
		} else if (variable == b) {
			for (Interval ci : c.getDomain()) {
				for (Interval aj : a.getDomain()) {
					addDifference(intervals, ci, aj);
				}
			}
		} else {
			for (Interval ai : a.getDomain()) {
				for (Interval bj : b.getDomain()) {
					addSum(intervals, ai, bj);
				}
			}
		}

		return variable.trySetValue(variable.getDomain().retainAll(new IntegerDomain().concat(intervals)));
	}

	/**
	 * Calculate `[a, b] + [c, d]' and add it to the list <tt>intervals</tt>.
	 *
	 * @param intervals The interval list.
	 * @param interval1 The interval [a, b].
	 * @param interval2 The interval [c, d].
	 */
	private void addSum(List<Interval> intervals, Interval interval1, Interval interval2) {
		int a = interval1.getLower();
		int b = interval1.getUpper();
		int c = interval2.getLower();
		int d = interval2.getUpper();

		int lower = Math.max(Interval.MIN_VALUE, a + c);
		int upper = Math.min(Interval.MAX_VALUE, b + d);

		intervals.add(new Interval(lower, upper));
	}

	/**
	 * Calculate `[a, b] - [c, d]' and add it to the list <tt>intervals</tt>.
	 *
	 * @param intervals The interval list.
	 * @param interval1 The interval [a, b].
	 * @param interval2 The interval [c, d].
	 */
	private void addDifference(List<Interval> intervals, Interval interval1, Interval interval2) {
		int a = interval1.getLower();
		int b = interval1.getUpper();
		int c = interval2.getLower();
		int d = interval2.getUpper();

		int lower = Math.max(Interval.MIN_VALUE, a - d);
		int upper = Math.min(Interval.MAX_VALUE, b - c);

		intervals.add(new Interval(lower, upper));
	}
}
