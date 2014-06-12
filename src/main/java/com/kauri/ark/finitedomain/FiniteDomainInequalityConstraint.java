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

/**
 * A constraint which forces a finite domain variable to be distinct from another finite domain variable.
 *
 * @author Eric Fritz
 */
public class FiniteDomainInequalityConstraint<T> implements Constraint<FiniteDomain<T>>
{
	/**
	 * The first variable.
	 */
	private Variable<FiniteDomain<T>> var1;

	/**
	 * The second variable.
	 */
	private Variable<FiniteDomain<T>> var2;

	/**
	 * Creates a new FiniteDomainInequalityConstraint.
	 *
	 * @param var1 The first variable.
	 * @param var2 The second variable.
	 */
	public FiniteDomainInequalityConstraint(Variable<FiniteDomain<T>> var1, Variable<FiniteDomain<T>> var2) {
		this.var1 = var1;
		this.var2 = var2;
	}

	@Override
	public boolean narrow(Variable<FiniteDomain<T>> variable) {
		// Narrow the domain of the argument variable to remove the unique element of the other domain. If the other
		// domain has not been narrowed to a unique value, we cannot narrow the argument domain without pruning some
		// valid assignment.

		FiniteDomain<T> domain1 = variable == var1 ? var1.getDomain() : var2.getDomain();
		FiniteDomain<T> domain2 = variable == var1 ? var2.getDomain() : var1.getDomain();

		if (!domain2.isUnique()) {
			return true;
		}

		return variable.trySetValue(domain1.removeAll(domain2));
	}
}
