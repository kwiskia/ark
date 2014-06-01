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

/**
 * FiniteDomainMappingConstraint
 *
 * @author Eric Fritz
 */
public class FiniteDomainMappingConstraint<T1, T2> implements Constraint<FiniteDomainVariable<?>>
{
	private FiniteDomainVariable<T1> var1;
	private FiniteDomainVariable<T2> var2;
	private Mapping<T1, T2> mapping;

	public FiniteDomainMappingConstraint(FiniteDomainVariable<T1> var1, FiniteDomainVariable<T2> var2, Mapping<T1, T2> mapping) {
		this.var1 = var1;
		this.var2 = var2;
		this.mapping = mapping;
	}

	@Override
	public boolean update(FiniteDomainVariable<?> variable) {
		if (variable == var1) {
			return var1.trySetValue(var1.getDomain().mapReverse(var2.getDomain(), mapping));
		} else if (variable == var2) {
			return var2.trySetValue(var2.getDomain().mapForward(var1.getDomain(), mapping));
		}

		throw new RuntimeException("Unreachable.");
	}
}
