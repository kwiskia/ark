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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * IntervalSet
 *
 * @author Eric Fritz
 */
public class IntervalSet implements Iterable<Interval>
{
	private List<Interval> intervals = new ArrayList<>();

	public IntervalSet() {
	}

	public IntervalSet(IntervalSet set) {
		for (Interval interval : set) {
			add(interval);
		}
	}

	public boolean isEmpty() {
		return intervals.isEmpty();
	}

	public boolean isUnique() {
		return intervals.size() == 1 && intervals.get(0).isUnique();
	}

	public int getMinimum() {
		return intervals.get(0).getLowerBound();
	}

	public int getMaximum() {
		return intervals.get(intervals.size() - 1).getUpperBound();
	}

	public void add(Interval interval) {
		intervals.add(interval);
		normalize();
	}

	public void remove(Interval interval) {
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

		this.intervals = newIntervals;
		normalize();
	}

	public void retainAll(IntervalSet other) {
		List<Interval> newIntervals = new ArrayList<>();

		for (Interval interval1 : this) {
			for (Interval interval2 : other) {
				int lower = Math.max(interval1.getLowerBound(), interval2.getLowerBound());
				int upper = Math.min(interval1.getUpperBound(), interval2.getUpperBound());

				if (lower <= upper) {
					newIntervals.add(new Interval(lower, upper));
				}
			}
		}

		this.intervals = newIntervals;
		normalize();
	}

	public void removeAll(IntervalSet other) {
		for (Interval interval : other) {
			remove(interval);
		}
	}

	public Iterator<Interval> iterator() {
		return intervals.iterator();
	}

	private void normalize() {
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

		Interval last = intervals.get(0);
		List<Interval> newIntervals = new ArrayList<>();

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
		this.intervals = newIntervals;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof IntervalSet)) {
			return false;
		}

		return intervals.equals(((IntervalSet) o).intervals);
	}
}
