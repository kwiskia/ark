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
 * A constraint which forces an integer variable to be the product of two other integer variables.
 *
 * @author Eric Fritz
 */
public class IntegerProductConstraint implements Constraint<IntegerDomain>
{
	/**
	 * The multiplicand variable.
	 */
	private Variable<IntegerDomain> a;

	/**
	 * The multiplier variable.
	 */
	private Variable<IntegerDomain> b;

	/**
	 * The product variable.
	 */
	private Variable<IntegerDomain> c;

	/**
	 * Creates a new IntegerProductConstraint.
	 *
	 * @param a The multiplicand variable.
	 * @param b The multiplier variable.
	 * @param c The product variable.
	 */
	public IntegerProductConstraint(Variable<IntegerDomain> a, Variable<IntegerDomain> b, Variable<IntegerDomain> c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public boolean narrow(Variable<IntegerDomain> variable) {
		// Narrow the domain of the argument variable to include (at most) the intervals satisfying:
		//   [when variable == a]: ci / bj for each interval ci in c and bj in b,
		//   [when variable == b]: ci / aj for each interval ci in c and aj in a,
		//   [when variable == c]: ai * bj for each interval ai in a and bj in b.

		// Unfortunately, interval division is undefined for denominators containing the value `0'. This complicates
		// the product identities `0 * b = c' and `a * 0 = c'. In the division cases, we can't attempt to narrow any
		// domain if c's domain contains zero and either
		//   1) b's domain contains zero (case ci / bj), or
		//   2) a's domain contains zero (case ci / aj).

		List<Interval> intervals = new ArrayList<>();

		if (variable == a) {
			for (Interval ci : c.getDomain()) {
				for (Interval bj : b.getDomain()) {
					if (bj.contains(0) && ci.contains(0)) {
						return true;
					}

					addQuotient(intervals, ci, bj);
				}
			}
		} else if (variable == b) {
			for (Interval ci : c.getDomain()) {
				for (Interval aj : a.getDomain()) {
					if (aj.contains(0) && ci.contains(0)) {
						return true;
					}

					addQuotient(intervals, ci, aj);
				}
			}
		} else {
			for (Interval ai : a.getDomain()) {
				for (Interval bj : b.getDomain()) {
					addProduct(intervals, ai, bj);
				}
			}
		}

		return variable.trySetValue(variable.getDomain().retainAll(new IntegerDomain().concat(intervals)));
	}

	/**
	 * Calculate `[a, b] * [c, d]' and add it to the list <tt>intervals</tt>.
	 *
	 * @param intervals The interval list.
	 * @param interval1 The interval [a, b].
	 * @param interval2 The interval [c, d].
	 */
	private void addProduct(List<Interval> intervals, Interval interval1, Interval interval2) {
		int a = interval1.getLower();
		int b = interval1.getUpper();
		int c = interval2.getLower();
		int d = interval2.getUpper();

		int lower = Math.max(Interval.MIN_VALUE, min(a * c, a * d, b * c, b * d));
		int upper = Math.min(Interval.MAX_VALUE, max(a * c, a * d, b * c, b * d));

		intervals.add(new Interval(lower, upper));
	}

	/**
	 * Calculate `[a, b] / [c, d]' and add it to the list <tt>intervals</tt>.
	 * <p/>
	 * This may add more than one interval to the list, as interval division is undefined when `[c, d]' contains `0'.
	 * In that case, split the interval into `[c, -1]' and `[1, d]' and perform division on both subsets.
	 *
	 * @param intervals The interval list.
	 * @param interval1 The interval [a, b].
	 * @param interval2 The interval [c, d].
	 */
	private void addQuotient(List<Interval> intervals, Interval interval1, Interval interval2) {
		int a = interval1.getLower();
		int b = interval1.getUpper();
		int c = interval2.getLower();
		int d = interval2.getUpper();

		if (interval2.getLower() <= 0 && 0 <= interval2.getUpper()) {
			if (c <= -1) addQuotient(intervals, a, b, c, -1);
			if (d >= +1) addQuotient(intervals, a, b, +1, d);
		} else {
			addQuotient(intervals, a, b, c, d);
		}
	}

	/**
	 * Calculate `[a, b] / [c, d]' and add it to teh list <tt>intervals</tt>.
	 * <p/>
	 * This assumes that `[c, d]' does not contain the value `0'.
	 *
	 * @param intervals The interval list.
	 * @param a         The lower bound of interval1.
	 * @param b         The upper bound of interval1.
	 * @param c         The lower bound of interval2.
	 * @param d         The upper bound of interval2.
	 */
	private void addQuotient(List<Interval> intervals, int a, int b, int c, int d) {
		int lower = min(a / c, a / d, b / c, b / d);
		int upper = max(a / c, a / d, b / c, b / d);

		intervals.add(new Interval(lower, upper));
	}

	private static int min(int a, int b, int c, int d) {
		return Math.min(a, Math.min(b, Math.min(c, d)));
	}

	private static int max(int a, int b, int c, int d) {
		return Math.max(a, Math.max(b, Math.max(c, d)));
	}
}
