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
 * A constraint which forces the values of two finite domain variables to exist in a bijective mapping from one
 * finite domain to another finite domain.
 *
 * @author Eric Fritz
 */
public class FiniteDomainMappingConstraint<T1, T2> implements Constraint
{
	/**
	 * The variable associated with the mapping's domain.
	 */
	private Variable<FiniteDomain<T1>> var1;

	/**
	 * The variable associated with the mapping's codomain.
	 */
	private Variable<FiniteDomain<T2>> var2;

	/**
	 * A complete bijective mapping from T1 to T2.
	 */
	private Mapping<T1, T2> mapping;

	/**
	 * Creates a new FiniteDomainMappingConstraint.
	 *
	 * @param var1    The variable associated with the mapping's domain.
	 * @param var2    The variable associated with the mapping's codomain.
	 * @param mapping A complete bijective mapping from T1 to T2.
	 */
	public FiniteDomainMappingConstraint(Variable<FiniteDomain<T1>> var1, Variable<FiniteDomain<T2>> var2, Mapping<T1, T2> mapping) {
		this.var1 = var1;
		this.var2 = var2;
		this.mapping = mapping;
	}

	@Override
	public boolean narrow(Variable variable) {
		// Narrow the domain of the argument variable to include (at most) the elements of a temporary domain
		// constructed by including the element `vi' where the domain of the other variable contains `vj' and
		//   1) (`vj', `vi') exists in the mapping (the forward case), or
		//   2) (`vi', `vj') exists in the mapping (the reverse case).

		FiniteDomain<T1> domain1 = var1.getDomain();
		FiniteDomain<T2> domain2 = var2.getDomain();

		if (variable == var1) {
			return variable.trySetValue(domain1.retainAll(domain1.mapReverse(domain2, mapping)));
		} else {
			return variable.trySetValue(domain2.retainAll(domain2.mapForward(domain1, mapping)));
		}
	}
}
