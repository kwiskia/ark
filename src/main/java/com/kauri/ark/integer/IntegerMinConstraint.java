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

/**
 * IntegerMinConstraint
 *
 * @author Eric Fritz
 */
public class IntegerMinConstraint implements Constraint<Variable<IntegerDomain>>
{
	private Variable<IntegerDomain> min;
	private Variable<IntegerDomain>[] variables;

	public IntegerMinConstraint(Variable<IntegerDomain> min, Variable<IntegerDomain>... variables) {
		this.min = min;
		this.variables = variables;
	}

	@Override
	public boolean update(Variable<IntegerDomain> variable) {
		if (variable == min) {
			int val = Interval.MAX_VALUE;
			for (Variable<IntegerDomain> v : variables) {
				val = Math.min(val, v.getDomain().getMinimum());
			}

			return variable.trySetValue(variable.getDomain().retain(new Interval(val, Interval.MAX_VALUE)));
		} else {
			return variable.trySetValue(variable.getDomain().retain(new Interval(min.getDomain().getMinimum(), Interval.MAX_VALUE)));
		}
	}
}
