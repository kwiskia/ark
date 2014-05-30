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
import java.util.Iterator;

/**
 * TempIntegerDomain
 *
 * @author Eric Fritz
 */
public class IntegerDomain implements Domain<IntervalSet>
{
	private IntervalSet set;

	public IntegerDomain(IntervalSet set) {
		this.set = set;
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean isUnique() {
		return set.isUnique();
	}

	public IntegerDomain add(Interval interval) {
		IntervalSet newSet = new IntervalSet(set);
		newSet.add(interval);
		return new IntegerDomain(newSet);
	}

	public IntegerDomain remove(Interval interval) {
		IntervalSet newSet = new IntervalSet(set);
		newSet.remove(interval);
		return new IntegerDomain(newSet);
	}

	public IntegerDomain retainAll(IntegerDomain other) {
		IntervalSet newSet = new IntervalSet(set);
		newSet.retainAll(other.set);
		return new IntegerDomain(newSet);
	}

	public IntegerDomain removeAll(IntegerDomain other) {
		IntervalSet newSet = new IntervalSet(set);
		newSet.removeAll(other.set);
		return new IntegerDomain(newSet);
	}

	@Override
	public UniqueValueIterator<Domain<IntervalSet>> getUniqueValues() {
		return new IntegerValueEnumerator(set);
	}

	public int getMinimum() {
		return set.getMinimum();
	}

	public int getMaximum() {
		return set.getMaximum();
	}

	private class IntegerValueEnumerator implements UniqueValueIterator<Domain<IntervalSet>>
	{
		private Iterator<Interval> iterator;
		private Interval current;
		private int value = 0;

		public IntegerValueEnumerator(IntervalSet set) {
			// TODO - make immutable
			this.iterator = new IntervalSet(set).iterator();

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

			IntervalSet set = new IntervalSet();
			set.add(new Interval(value, value++));
			return new IntegerDomain(set);
		}
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof IntegerDomain)) {
			return false;
		}

		return set.equals(((IntegerDomain) o).set);
	}
}
