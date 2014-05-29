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

import com.kauri.ark.Solver;
import com.kauri.ark.ValueEnumerator;
import com.kauri.ark.Variable;

/**
 * IntervalVariable
 *
 * @author Eric Fritz
 */
public class IntegerVariable extends Variable<Interval>
{
	public IntegerVariable(Solver solver, int low, int high) {
		super(solver, new Interval(low, high));
	}

	@Override
	public boolean isEmpty() {
		return getCurrentAllowableValues().isEmpty();
	}

	@Override
	public boolean isUnique() {
		return getCurrentAllowableValues().isUnique();
	}

	public int getAssignment() {
		if (!isUnique()) {
			throw new RuntimeException("Assignment not unique.");
		}

		return getCurrentAllowableValues().getLowerBound();
	}

	@Override
	public ValueEnumerator getValueEnumerator() {
		return new IntegerValueEnumerator(this, getSolver(), getSolver().saveValues());
	}
}
