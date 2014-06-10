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
 * ProductConstraint
 *
 * @author Eric Fritz
 */
public class IntegerProductConstraint implements Constraint<IntegerDomain>
{
	private Variable<IntegerDomain> a;
	private Variable<IntegerDomain> b;
	private Variable<IntegerDomain> c;

	public IntegerProductConstraint(Variable<IntegerDomain> a, Variable<IntegerDomain> b, Variable<IntegerDomain> c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public boolean update(Variable<IntegerDomain> variable) {
		List<Interval> intervals = new ArrayList<>();

		if (variable == a) {
			// a = c / b
			for (Interval interval2 : b.getDomain()) {
				for (Interval interval3 : c.getDomain()) {
					if (interval2.contains(0) && interval3.contains(0)) {
						return true;
					}

					addQuotient(intervals, interval3, interval2);
				}
			}
		} else if (variable == b) {
			// b = c / a
			for (Interval interval1 : a.getDomain()) {
				for (Interval interval3 : c.getDomain()) {
					if (interval1.contains(0) && interval3.contains(0)) {
						return true;
					}

					addQuotient(intervals, interval3, interval1);
				}
			}
		} else {
			// c = a * b
			for (Interval interval1 : a.getDomain()) {
				for (Interval interval2 : b.getDomain()) {
					addProduct(intervals, interval1, interval2);
				}
			}
		}

		return variable.trySetValue(variable.getDomain().retainAll(new IntegerDomain().concat(intervals)));
	}

	private void addProduct(List<Interval> intervals, Interval interval1, Interval interval2) {
		int a = interval1.getLower();
		int b = interval1.getUpper();
		int c = interval2.getLower();
		int d = interval2.getUpper();

		addProduct(intervals, a, b, c, d);
	}

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

	private void addProduct(List<Interval> intervals, int a, int b, int c, int d) {
		int lower = Math.max(Interval.MIN_VALUE, min(a * c, a * d, b * c, b * d));
		int upper = Math.min(Interval.MAX_VALUE, max(a * c, a * d, b * c, b * d));

		intervals.add(new Interval(lower, upper));
	}

	private void addQuotient(List<Interval> intervals, int a, int b, int c, int d) {
		int lower = min(a / c, a / d, b / c, b / d);
		int upper = max(a / c, a / d, b / c, b / d);

		intervals.add(new Interval(lower, upper));
	}

	private int min(int a, int b, int c, int d) {
		return Math.min(a, Math.min(b, Math.min(c, d)));
	}

	private int max(int a, int b, int c, int d) {
		return Math.max(a, Math.max(b, Math.max(c, d)));
	}
}
