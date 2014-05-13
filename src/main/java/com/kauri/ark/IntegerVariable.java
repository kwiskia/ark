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

import java.util.Stack;

/**
 * IntervalVariable
 *
 * @author Eric Fritz
 */
public class IntegerVariable extends Variable<Interval>
{
	public IntegerVariable(Solver solver, Interval interval) {
		super(solver, interval);
	}

	@Override
	public boolean isEmpty() {
		return getAllowableValues().isEmpty();
	}

	@Override
	public boolean isUnique() {
		return getAllowableValues().isUnique();
	}

	public int getAssignment() {
		if (!isUnique()) {
			throw new RuntimeException("Assignment not unique.");
		}

		return getAllowableValues().getLowerBound();
	}

	@Override
	public ValueEnumerator getValueEnumerator() {
		return new IntervalValueEnumerator(getSolver(), getSolver().saveValues());
	}

	private class IntervalValueEnumerator implements ValueEnumerator
	{
		private Solver solver;
		private int mark;
		private boolean hasAdvanced = false;

		private Stack<Interval> candidates = new Stack<>();

		public IntervalValueEnumerator(Solver solver, int mark) {
			this.solver = solver;
			this.mark = mark;

			candidates.push(getAllowableValues());
		}

		@Override
		public boolean advance() {
			if (hasAdvanced) {
				solver.restore(mark);
			}

			while (!candidates.isEmpty()) {
				Interval candidate = candidates.pop();

				if (trySetValue(candidate) && solver.resolveConstraints()) {
					if (candidate.isUnique()) {
						hasAdvanced = true;
						return true;
					} else {
						int mid = candidate.getLowerBound() + candidate.getRange() / 2;

						Interval lower = new Interval(candidate.getLowerBound(), mid);
						Interval upper = new Interval(mid + 1, candidate.getUpperBound());

						if (solver.getExpansionOrder() == Solver.ExpansionOrder.RANDOM && Math.random() < .5) {
							Interval t = lower;
							lower = upper;
							upper = t;
						}

						if (!candidate.equals(upper)) candidates.push(upper);
						if (!candidate.equals(lower)) candidates.push(lower);
					}
				}

				solver.restore(mark);
			}

			return false;
		}
	}
}
