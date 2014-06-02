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
import java.util.ArrayList;
import java.util.Arrays;
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
	public Iterator<Domain<Integer>> getUniqueValues() {
		return new DomainIterator(intervals.iterator());
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

	private class DomainIterator implements Iterator<Domain<Integer>>
	{
		private Iterator<Interval> iterator;
		private Interval current;
		private int value = 0;
		private boolean hasNext;

		public DomainIterator(Iterator<Interval> iterator) {
			this.iterator = iterator;

			getNext();
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public IntegerDomain next() {
			Interval interval = new Interval(value, value);
			getNext();
			return new IntegerDomain(interval);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void getNext() {
			if (current == null) {
				if (iterator.hasNext()) {
					current = iterator.next();
					value = current.getLower();
					hasNext = true;
				} else {
					hasNext = false;
				}
			} else {
				value++;
				if (value > current.getUpper()) {
					if (iterator.hasNext()) {
						current = iterator.next();
						value = current.getLower();
						hasNext = true;
					} else {
						hasNext = false;
					}
				} else {
					hasNext = true;
				}
			}
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
