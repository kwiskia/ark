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

import com.kauri.ark.Solver;
import com.kauri.ark.ValueEnumerator;
import com.kauri.ark.Variable;
import java.util.BitSet;
import java.util.List;

/**
 * FiniteDomainVariable
 *
 * @author Eric Fritz
 */
public class FiniteDomainVariable<T> extends Variable<BitSet>
{
	public static <T> FiniteDomainVariable<T> create(Solver solver, List<T> elements) {
		return new FiniteDomainVariable<>(solver, new FiniteDomain<>(elements));
	}

	private FiniteDomain<T> finiteDomain;

	public FiniteDomainVariable(Solver solver, FiniteDomain<T> finiteDomain) {
		super(solver, finiteDomain.createBitSet());
		this.finiteDomain = finiteDomain;
	}

	public FiniteDomain<T> getFiniteDomain() {
		return finiteDomain;
	}

	@Override
	public boolean isEmpty() {
		return getCurrentAllowableValues().cardinality() == 0;
	}

	@Override
	public boolean isUnique() {
		return getCurrentAllowableValues().cardinality() == 1;
	}

	public T getAssignment() {
		if (!isUnique()) {
			throw new RuntimeException("Assignment not unique.");
		}

		return finiteDomain.getValue(getCurrentAllowableValues().nextSetBit(0));
	}

	@Override
	public ValueEnumerator getValueEnumerator() {
		return new FiniteDomainValueEnumerator(this.getCurrentAllowableValues());
	}

	private class FiniteDomainValueEnumerator implements ValueEnumerator<BitSet>
	{
		private BitSet bitset;
		private int k = -1;

		public FiniteDomainValueEnumerator(BitSet bitset) {
			this.bitset = bitset;
		}

		@Override
		public BitSet next() {
			k = bitset.nextSetBit(k + 1);

			if (k == -1) {
				return null;
			}

			BitSet bs = new BitSet(bitset.size());
			bs.set(k);
			return bs;
		}
	}
}
