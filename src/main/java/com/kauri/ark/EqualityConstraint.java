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

import java.util.ArrayList;
import java.util.List;

/**
 * EqualityConstraint
 *
 * @author Eric Fritz
 */
public class EqualityConstraint<T> extends Constraint<FiniteDomainVariable<T>>
{
	public EqualityConstraint(FiniteDomainVariable<T>... variables) {
		super(variables);
	}

	public boolean updateVariable(Solver solver, FiniteDomainVariable<T> variable) {
		List<T> values = new ArrayList<>(variable.allowableValues);

		for (FiniteDomainVariable<T> v : variables) {
			if (v != variable) {
				if (v.isUnique()) {
					values.retainAll(v.allowableValues);
				}
			}
		}

		return variable.trySetValue(solver, values);
	}
}
