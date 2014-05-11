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

package com.kauri.ark;

import java.util.Iterator;
import java.util.Stack;

/**
 * IntervalVariable
 *
 * @author Eric Fritz
 */
public class IntegerVariable extends Variable<Interval>
{
	public IntegerVariable(String name, Interval interval) {
		super(name, interval);
	}

	@Override
	public Iterator<Interval> getUniqueValues(Solver solver) {
		return new IntervalIterator(solver, solver.saveValues());
	}

	@Override
	public boolean isEmpty() {
		return allowableValues.isEmpty();
	}

	@Override
	public boolean isUnique() {
		return allowableValues.isUnique();
	}

	@Override
	public Integer getUniqueValue() {
		if (!isUnique()) {
			throw new RuntimeException("Not unique.");
		}

		return allowableValues.getLowerBound();
	}

	private class IntervalIterator implements Iterator<Interval>
	{
		private Solver solver;
		private int mark;
		private boolean hasReturned = false;

		private Stack<Interval> candidates = new Stack<>();

		public IntervalIterator(Solver solver, int mark) {
			this.solver = solver;
			this.mark = mark;

			candidates.push(allowableValues);
		}

		@Override
		public boolean hasNext() {
			if (hasReturned) {
				solver.restore(mark);
			}

			while (!candidates.isEmpty()) {
				Interval candidate = candidates.pop();

				if (trySetAndResolveConstraints(solver, candidate)) {
					hasReturned = candidate.isUnique();

					if (candidate.isUnique()) {
						return true;
					} else {
						int mid = candidate.getLowerBound() + candidate.getRange() / 2;

						Interval lower = new Interval(candidate.getLowerBound(), mid);
						Interval upper = new Interval(mid + 1, candidate.getUpperBound());

						if (!candidate.equals(upper)) candidates.push(upper);
						if (!candidate.equals(lower)) candidates.push(lower);
					}
				}

				solver.restore(mark);
			}

			return false;
		}

		@Override
		public Interval next() {
			return allowableValues;
		}
	}
}
