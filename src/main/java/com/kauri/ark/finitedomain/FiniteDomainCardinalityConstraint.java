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
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * CardinalityConstraint
 *
 * @author Eric Fritz
 */
public class FiniteDomainCardinalityConstraint<T> implements Constraint<FiniteDomainVariable<T>>
{
	private int min;
	private int max;
	private BitSet mask;
	private List<FiniteDomainVariable<T>> variables;

	public FiniteDomainCardinalityConstraint(T value, int min, int max, FiniteDomainVariable<T>... variables) {
		this.variables = Arrays.asList(variables);

		this.min = min;
		this.max = max;

		mask = variables[0].getFiniteDomain().createBitSet(value);
	}

	@Override
	public boolean update(FiniteDomainVariable<T> variable) {
		int possible = 0;
		int definite = 0;

		for (FiniteDomainVariable<T> v : variables) {
			if (v.getCurrentAllowableValues().intersects(mask)) {
				possible++;

				if (v.isUnique()) {
					definite++;
				}
			}
		}

		if (possible < min || definite > max) {
			return false;
		}

		if (definite == max) {
			for (FiniteDomainVariable<T> v : variables) {
				if (v.getCurrentAllowableValues().intersects(mask) && !v.isUnique()) {
					BitSet bs = (BitSet) v.getCurrentAllowableValues().clone();
					bs.andNot(mask);

					if (!v.trySetValue(bs)) {
						return false;
					}
				}
			}
		}

		return true;
	}
}
