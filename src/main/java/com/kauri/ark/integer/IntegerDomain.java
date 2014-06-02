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
import com.kauri.ark.DomainIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * TempIntegerDomain
 *
 * @author Eric Fritz
 */
public class IntegerDomain implements Domain<Integer>
{
	private List<Interval> intervals = new ArrayList<>();
	private int size;

	public IntegerDomain(Interval interval) {
		this(Arrays.asList(interval));
	}

	public IntegerDomain(List<Interval> intervals) {
		this.intervals = intervals;

		int size = 0;
		for (Interval interval : intervals) {
			size += interval.getUpper() - interval.getLower() + 1;
		}

		this.size = size;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean isUnique() {
		return size == 1;
	}

	public IntegerDomain retain(Interval interval) {
		List<Interval> newIntervals = new ArrayList<>();

		for (Interval interval1 : intervals) {
			Interval intersection = interval1.intersection(interval);

			if (intersection != null) {
				newIntervals.add(intersection);
			}
		}
		return new IntegerDomain(newIntervals);
	}

	public IntegerDomain remove(Interval interval) {
		return new IntegerDomain(remove(intervals, interval));
	}

	public IntegerDomain retainAll(IntegerDomain other) {
		List<Interval> newIntervals = new ArrayList<>();

		for (Interval interval1 : intervals) {
			for (Interval interval2 : other.intervals) {
				Interval intersection = interval1.intersection(interval2);

				if (intersection != null) {
					newIntervals.add(intersection);
				}
			}
		}

		return new IntegerDomain(newIntervals);
	}

	public IntegerDomain removeAll(IntegerDomain other) {
		return new IntegerDomain(removeAll(intervals, other.intervals));
	}

	@Override
	public DomainIterator<Integer> getUniqueValues() {
		return new IntegerDomainIterator();
	}

	public int getMinimum() {
		return intervals.get(0).getLower();
	}

	public int getMaximum() {
		return intervals.get(intervals.size() - 1).getUpper();
	}

	@Override
	public Integer getUniqueValue() {
		if (!isUnique()) {
			throw new RuntimeException("Domain has not been narrowed to a unique value.");
		}

		return getMinimum();
	}

	private class IntegerDomainIterator implements DomainIterator<Integer>
	{
		private Stack<Interval> candidates;
		private Interval last;

		public IntegerDomainIterator() {
			candidates = new Stack<>();

			for (Interval interval : intervals) {
				candidates.push(interval);
			}
		}

		@Override
		public boolean hasNext() {
			return !candidates.isEmpty();
		}

		@Override
		public IntegerDomain next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			last = candidates.pop();
			return new IntegerDomain(last);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void lastDomainValid() {
			int center = last.getLower() + (last.getUpper() - last.getLower()) / 2;

			int lower1 = last.getLower();
			int upper1;
			int lower2;
			int upper2 = last.getUpper();

			if (center == last.getUpper()) {
				upper1 = center - 1;
				lower2 = center;
			} else {
				upper1 = center;
				lower2 = center + 1;
			}

			if (lower1 <= upper1 && (lower1 != last.getLower() || upper1 != last.getUpper())) {
				candidates.add(new Interval(lower1, upper1));
			}

			if (lower2 <= upper2 && (lower2 != last.getLower() || upper2 != last.getUpper())) {
				candidates.add(new Interval(lower2, upper2));
			}
		}
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof IntegerDomain)) {
			return false;
		}

		return intervals.equals(((IntegerDomain) o).intervals);
	}

	public String toString() {
		return intervals.toString();
	}

	//
	// Interval Implementation

	private List<Interval> remove(List<Interval> intervals, Interval interval) {
		List<Interval> newIntervals = new ArrayList<>();

		for (int i = 0; i < intervals.size(); i++) {
			Interval i1 = intervals.get(i);
			Interval i2 = interval;

			if (i1.getUpper() < i2.getLower() || i2.getUpper() < i1.getLower()) {
				newIntervals.add(i1);
			} else {
				int l1 = i1.getLower();
				int h1 = i2.getLower() - 1;

				int l2 = i2.getUpper() + 1;
				int h2 = i1.getUpper();

				if (l1 <= h1) newIntervals.add(new Interval(l1, h1));
				if (l2 <= h2) newIntervals.add(new Interval(l2, h2));
			}
		}

		return newIntervals;
	}

	private List<Interval> removeAll(List<Interval> intervals1, List<Interval> intervals2) {
		//
		// TODO - can do in single pass?
		//

		for (Interval interval : intervals2) {
			intervals1 = remove(intervals1, interval);
		}

		return intervals1;
	}
}
