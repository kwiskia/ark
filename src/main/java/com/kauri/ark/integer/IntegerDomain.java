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

import com.kauri.ark.Domain;
import com.kauri.ark.UniqueValueIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * TempIntegerDomain
 *
 * @author Eric Fritz
 */
public class IntegerDomain implements Domain<Integer>
{
	private List<Interval> intervals = new ArrayList<>();

	public IntegerDomain(Interval interval) {
		this(Arrays.asList(interval));
	}

	public IntegerDomain(List<Interval> intervals) {
		this.intervals = intervals;
	}

	@Override
	public boolean isEmpty() {
		return intervals.isEmpty();
	}

	@Override
	public boolean isUnique() {
		return intervals.size() == 1 && intervals.get(0).isUnique();
	}

	public IntegerDomain add(Interval interval) {
		List<Interval> newIntervals = new ArrayList<>(intervals);
		newIntervals.add(interval);
		normalize(newIntervals);

		return new IntegerDomain(newIntervals);
	}

	public IntegerDomain remove(Interval interval) {
		return new IntegerDomain(normalize(remove(intervals, interval)));
	}

	public IntegerDomain retainAll(IntegerDomain other) {
		return new IntegerDomain(normalize(retainAll(intervals, other.intervals)));
	}

	public IntegerDomain removeAll(IntegerDomain other) {
		return new IntegerDomain(normalize(removeAll(intervals, other.intervals)));
	}

	@Override
	public UniqueValueIterator<Domain<Integer>> getUniqueValues() {
		return new IntegerValueEnumerator(intervals.iterator());
	}

	public int getMinimum() {
		return intervals.get(0).getLowerBound();
	}

	public int getMaximum() {
		return intervals.get(intervals.size() - 1).getUpperBound();
	}

	public Integer getUniqueValue() {
		if (!isUnique()) {
			throw new RuntimeException("Domain has not been narrowed to a unique value.");
		}

		return getMinimum();
	}

	private class IntegerValueEnumerator implements UniqueValueIterator<Domain<Integer>>
	{
		private Iterator<Interval> iterator;
		private Interval current;
		private int value = 0;

		public IntegerValueEnumerator(Iterator<Interval> iterator) {
			this.iterator = iterator;

			if (iterator.hasNext()) {
				current = iterator.next();
				value = current.getLowerBound();
			}
		}

		@Override
		public IntegerDomain next() {
			if (current == null) {
				return null;
			}

			if (value > current.getUpperBound()) {
				if (!iterator.hasNext()) {
					return null;
				}

				current = iterator.next();
				value = current.getLowerBound();
			}

			return new IntegerDomain(new Interval(value, value++));
		}
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof IntegerDomain)) {
			return false;
		}

		return intervals.equals(((IntegerDomain) o).intervals);
	}

	//
	// Interval Implementation

	private List<Interval> remove(List<Interval> intervals, Interval interval) {
		List<Interval> newIntervals = new ArrayList<>();

		for (int i = 0; i < intervals.size(); i++) {
			Interval i1 = intervals.get(i);
			Interval i2 = interval;

			if (i1.getUpperBound() < i2.getLowerBound() || i2.getUpperBound() < i1.getLowerBound()) {
				// non-intersecting case

				newIntervals.add(i1);
				continue;
			}

			// [i1]  --
			// [i2] ----
			// skip entire interval

			if (i2.getLowerBound() <= i1.getLowerBound() && i1.getUpperBound() <= i2.getUpperBound()) {
				continue;
			}

			// [i1] ----
			// [i2]  --
			// // will make 2 parts

			if (i1.getLowerBound() <= i2.getLowerBound() && i2.getUpperBound() <= i1.getUpperBound()) {
				newIntervals.add(new Interval(i1.getLowerBound(), i2.getLowerBound() - 1));
				newIntervals.add(new Interval(i2.getUpperBound() + 1, i1.getUpperBound()));
				continue;
			}

			// [i1] ----
			// [i2]   ----
			// will make left part

			if (i1.getLowerBound() <= i2.getLowerBound() && i1.getUpperBound() <= i2.getUpperBound()) {
				newIntervals.add(new Interval(i1.getLowerBound(), i2.getLowerBound() - 1));
				continue;
			}

			// [i1]   ----
			// [i2] ----
			// will make right part

			if (i2.getLowerBound() <= i1.getLowerBound() && i2.getUpperBound() <= i1.getUpperBound()) {
				newIntervals.add(new Interval(i2.getUpperBound() + 1, i1.getUpperBound()));
				continue;
			}
		}

		return newIntervals;
	}

	private List<Interval> retainAll(List<Interval> intervals1, List<Interval> intervals2) {
		List<Interval> newIntervals = new ArrayList<>();

		for (Interval interval1 : intervals) {
			for (Interval interval2 : intervals2) {
				int lower = Math.max(interval1.getLowerBound(), interval2.getLowerBound());
				int upper = Math.min(interval1.getUpperBound(), interval2.getUpperBound());

				if (lower <= upper) {
					newIntervals.add(new Interval(lower, upper));
				}
			}
		}

		return newIntervals;
	}

	private List<Interval> removeAll(List<Interval> intervals1, List<Interval> intervals2) {
		for (Interval interval : intervals2) {
			intervals1 = remove(intervals1, interval);
		}

		return intervals1;
	}

	private List<Interval> normalize(List<Interval> intervals) {
		Iterator<Interval> itr = intervals.iterator();

		while (itr.hasNext()) {
			if (itr.next().isEmpty()) {
				itr.remove();
			}
		}

		intervals.sort(new Comparator<Interval>()
		{
			@Override
			public int compare(Interval o1, Interval o2) {
				Integer l1 = o1.getLowerBound();
				Integer l2 = o2.getLowerBound();

				return l1.compareTo(l2);
			}
		});

		List<Interval> newIntervals = new ArrayList<>();

		if (intervals.size() > 0) {
			Interval last = intervals.get(0);

			for (int i = 1; i < intervals.size(); i++) {
				Interval i1 = last;
				Interval i2 = intervals.get(i);

				if (i2.getLowerBound() <= i1.getUpperBound()) {
					last = new Interval(i1.getLowerBound(), i2.getUpperBound());
				} else {
					newIntervals.add(last);
					last = i2;
				}
			}

			newIntervals.add(last);
		}

		return newIntervals;
	}
}
