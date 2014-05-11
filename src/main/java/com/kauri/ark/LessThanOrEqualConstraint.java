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

import java.util.BitSet;

/**
 * LessThanOrEqualConstraint
 *
 * @author Eric Fritz
 */
public class LessThanOrEqualConstraint<T> extends Constraint<FiniteDomainVariable<T>>
{
	public LessThanOrEqualConstraint(FiniteDomainVariable<T> var1, FiniteDomainVariable<T> var2) {
		super(var1, var2);
	}

	@Override
	public boolean updateVariable(Solver solver, FiniteDomainVariable<T> variable) {
		FiniteDomainVariable<T> other = variable == variables.get(0) ? variables.get(1) : variables.get(0);

		BitSet bs = variable.allowableValues.get(0, variable.allowableValues.size());

		if (variable == variables.get(0)) {
			// remove everything greater than to the largest bit in other
			bs.clear(other.allowableValues.length(), bs.size());
		} else {
			// remove everything smaller equal to smallest bit in other
			bs.clear(0, other.allowableValues.nextSetBit(0));
		}

		return variable.trySetValue(solver, bs);
	}
}
