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
 * A constraint which forces an integer variable to count the number of times a specific value is assigned to a set of
 * finite domain variables.
 *
 * @author Eric Fritz
 */
public class FiniteDomainCardinalityConstraint<T> implements Constraint
{
	/**
	 * The target value.
	 */
	private T value;

	/**
	 * The counter variable.
	 */
	private IntegerVariable counter;

	/**
	 * The variables whose assignments are counted.
	 */
	private Variable<FiniteDomain<T>>[] variables;

	/**
	 * Creates a new FiniteDomainCardinalityConstraint.
	 *
	 * @param value     The target value.
	 * @param counter   The counter variable.
	 * @param variables The variables whose assignments are counted.
	 */
	public FiniteDomainCardinalityConstraint(T value, IntegerVariable counter, Variable<FiniteDomain<T>>... variables) {
		this.value = value;
		this.counter = counter;
		this.variables = variables;
	}

	@Override
	public boolean narrow(Variable variable) {
		int definite = 0;
		int possible = 0;

		// Count the number of variables which can possibly be assigned the target value (variables with the target
		// value in its domain), and the number of variables which are already assigned the target value (variables
		// with the target value in its singleton domain).

		for (Variable<FiniteDomain<T>> v : variables) {
			if (v.getDomain().contains(value)) {
				possible++;

				if (v.getDomain().isUnique()) {
					definite++;
				}
			}
		}

		// If the counter's domain is disjoint from [definite, possible], then the counter is not consistent with
		// the current assignments of our variable set. Return false in this case.

		if (possible < counter.getDomain().getMinimum() || definite > counter.getDomain().getMaximum()) {
			return false;
		}

		// If we are narrowing the domain of the counter, bound its domain to [definite, possible]. Otherwise, see if
		// we can assign the argument variable the target value. This is allowable only if either the counter's lower
		// bound is equal to possible, or the counter's upper bound is equal to definite. Every domain containing the
		// target value must have the target value as its unique value in the first case, and every domain containing
		// the target value must not have the target value as its unique value in the second case.

		if (variable == counter) {
			return counter.trySetValue(counter.getDomain().retain(new Interval(definite, possible)));
		} else {
			Variable<FiniteDomain<T>> v = variable;

			if (v.getDomain().contains(value) && !v.getDomain().isUnique()) {
				if (possible == counter.getDomain().getMinimum()) {
					return v.trySetValue(v.getDomain().retain(value));
				}

				if (definite == counter.getDomain().getMaximum()) {
					return v.trySetValue(v.getDomain().remove(value));
				}
			}

			return true;
		}
	}
}
