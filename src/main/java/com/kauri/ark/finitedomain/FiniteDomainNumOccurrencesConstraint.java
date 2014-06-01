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

package com.kauri.ark.finitedomain;

import com.kauri.ark.Constraint;
import com.kauri.ark.Variable;
import com.kauri.ark.integer.IntegerVariable;
import com.kauri.ark.integer.Interval;

/**
 * FiniteDomainNumOccurrencesConstraint
 *
 * @author Eric Fritz
 */
public class FiniteDomainNumOccurrencesConstraint<T> implements Constraint
{
	private T value;
	private IntegerVariable counter;
	private Variable<FiniteDomain<T>>[] variables;

	public FiniteDomainNumOccurrencesConstraint(T value, IntegerVariable counter, Variable<FiniteDomain<T>>... variables) {
		this.value = value;
		this.counter = counter;
		this.variables = variables;
	}

	@Override
	public boolean update(Variable variable) {
		int possible = 0;
		int definite = 0;

		for (Variable<FiniteDomain<T>> v : variables) {
			if (v.getDomain().contains(value)) {
				possible++;

				if (v.getDomain().isUnique()) {
					definite++;
				}
			}
		}

		if (variable == counter) {
			return counter.trySetValue(counter.getDomain().retain(new Interval(definite, possible)));
		} else {
			if (counter.getDomain().isUnique()) {
				int target = counter.getDomain().getUniqueValue();

				if (possible < target || definite > target) {
					return false;
				}
			}

			return true;
		}
	}
}
