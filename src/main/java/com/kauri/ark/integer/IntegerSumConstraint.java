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
 * SumConstraint
 *
 * @author Eric Fritz
 */
public class IntegerSumConstraint implements Constraint<IntegerDomain>
{
	private Variable<IntegerDomain> a;
	private Variable<IntegerDomain> b;
	private Variable<IntegerDomain> c;

	public IntegerSumConstraint(Variable<IntegerDomain> a, Variable<IntegerDomain> b, Variable<IntegerDomain> c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public boolean update(Variable<IntegerDomain> variable) {
		List<Interval> intervals = new ArrayList<>();

		if (variable == a) {
			// a = c - b
			for (Interval interval2 : b.getDomain()) {
				for (Interval interval3 : c.getDomain()) {
					addDifference(intervals, interval3, interval2);
				}
			}
		} else if (variable == b) {
			// b = c - a
			for (Interval interval1 : a.getDomain()) {
				for (Interval interval3 : c.getDomain()) {
					addDifference(intervals, interval3, interval1);
				}
			}
		} else {
			// c = a + b
			for (Interval interval1 : a.getDomain()) {
				for (Interval interval2 : b.getDomain()) {
					addSum(intervals, interval1, interval2);
				}
			}
		}

		return variable.trySetValue(variable.getDomain().retainAll(new IntegerDomain().concat(intervals)));
	}

	private void addDifference(List<Interval> intervals, Interval interval1, Interval interval2) {
		int a = interval1.getLower();
		int b = interval1.getUpper();
		int c = interval2.getLower();
		int d = interval2.getUpper();

		addDifference(intervals, a, b, c, d);
	}

	private void addSum(List<Interval> intervals, Interval interval1, Interval interval2) {
		int a = interval1.getLower();
		int b = interval1.getUpper();
		int c = interval2.getLower();
		int d = interval2.getUpper();

		addSum(intervals, a, b, c, d);
	}

	private void addDifference(List<Interval> intervals, int a, int b, int c, int d) {
		int lower = Math.max(Interval.MIN_VALUE, a - d);
		int upper = Math.min(Interval.MAX_VALUE, b - c);

		intervals.add(new Interval(lower, upper));
	}

	private void addSum(List<Interval> intervals, int a, int b, int c, int d) {
		int lower = Math.max(Interval.MIN_VALUE, a + c);
		int upper = Math.min(Interval.MAX_VALUE, b + d);

		intervals.add(new Interval(lower, upper));
	}
}
