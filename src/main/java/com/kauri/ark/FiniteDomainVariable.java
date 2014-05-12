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

import java.util.BitSet;

/**
 * FiniteDomainVariable
 *
 * @author Eric Fritz
 */
public class FiniteDomainVariable<T> extends Variable<BitSet>
{
	private FiniteDomain<T> finiteDomain;

	public FiniteDomainVariable(String name, FiniteDomain<T> finiteDomain) {
		super(name, finiteDomain.createBitSet());
		this.finiteDomain = finiteDomain;
	}

	public FiniteDomain<T> getFiniteDomain() {
		return finiteDomain;
	}

	@Override
	public boolean isEmpty() {
		return allowableValues.cardinality() == 0;
	}

	@Override
	public boolean isUnique() {
		return allowableValues.cardinality() == 1;
	}

	@Override
	public T getUniqueValue() {
		if (!isUnique()) {
			throw new RuntimeException("Not unique.");
		}

		return finiteDomain.getValue(allowableValues.nextSetBit(0));
	}

	@Override
	public ValueEnumerator getUniqueValues(Solver solver) {
		return new FiniteDomainValueEnumerator(solver, solver.saveValues());
	}

	private class FiniteDomainValueEnumerator implements ValueEnumerator
	{
		private Solver solver;
		private int mark;
		private boolean hasAdvanced = false;

		private int next = -1;

		public FiniteDomainValueEnumerator(Solver solver, int mark) {
			this.solver = solver;
			this.mark = mark;
		}

		@Override
		public boolean advance() {
			if (hasAdvanced) {
				solver.restore(mark);
			}

			while (allowableValues.nextSetBit(next + 1) != -1) {
				next = allowableValues.nextSetBit(next + 1);

				BitSet bs = new BitSet(allowableValues.size());
				bs.set(next);

				if (trySetAndResolveConstraints(solver, bs)) {
					hasAdvanced = true;
					return true;
				}

				solver.restore(mark);
			}

			return false;
		}
	}
}
