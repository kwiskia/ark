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
 * InequalityConstraint
 *
 * @author Eric Fritz
 */
public class FiniteDomainInequalityConstraint<T> implements Constraint<Variable<FiniteDomain<T>>>
{
	private Variable<FiniteDomain<T>>[] variables;

	public FiniteDomainInequalityConstraint(Variable<FiniteDomain<T>>... variables) {
		this.variables = variables;
	}

	@Override
	public boolean update(Variable<FiniteDomain<T>> variable) {
		FiniteDomain<T> domain = variable.getDomain();

		for (Variable<FiniteDomain<T>> v : variables) {
			if (v != variable && v.getDomain().isUnique()) {
				domain = domain.removeAll(v.getDomain());
			}
		}

		return variable.trySetValue(domain);
	}
}
