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

	public FiniteDomainVariable(Solver solver, FiniteDomain<T> finiteDomain) {
		super(solver, finiteDomain.createBitSet());
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
	public ValueEnumerator getValueEnumerator() {
		return new FiniteDomainValueEnumerator(getSolver(), getSolver().saveValues());
	}

	private class FiniteDomainValueEnumerator implements ValueEnumerator
	{
		private Solver solver;
		private int mark;
		private int[] indices;
		private int k = 0;

		public FiniteDomainValueEnumerator(Solver solver, int mark) {
			this.solver = solver;
			this.mark = mark;

			indices = new int[allowableValues.cardinality()];

			int j = 0;
			for (int i = allowableValues.nextSetBit(0); i != -1; i = allowableValues.nextSetBit(i + 1)) {
				indices[j++] = i;
			}

			if (solver.getExpansionOrder() == Solver.ExpansionOrder.RANDOM) {
				for (int i = indices.length - 1; i >= 0; i--) {
					j = (int) (Math.random() * (i + 1));

					int t = indices[j];
					indices[j] = indices[i];
					indices[i] = t;
				}
			}
		}

		@Override
		public boolean advance() {
			if (k > 0) {
				solver.restore(mark);
			}

			while (k < indices.length) {
				BitSet bs = new BitSet(allowableValues.size());
				bs.set(indices[k++]);

				if (trySetAndResolveConstraints(bs)) {
					return true;
				}

				solver.restore(mark);
			}

			return false;
		}
	}
}
