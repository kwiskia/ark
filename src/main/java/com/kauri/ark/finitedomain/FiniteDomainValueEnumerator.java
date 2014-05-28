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
import java.util.BitSet;

/**
 * FiniteDomainValueEnumerator
 *
 * @author Eric Fritz
 */
class FiniteDomainValueEnumerator<T> implements ValueEnumerator
{
	private FiniteDomainVariable<T> finiteDomainVariable;
	private Solver solver;
	private int mark;
	private int[] indices;
	private int k = 0;

	public FiniteDomainValueEnumerator(FiniteDomainVariable<T> finiteDomainVariable, Solver solver, int mark) {
		this.finiteDomainVariable = finiteDomainVariable;
		this.solver = solver;
		this.mark = mark;

		indices = new int[finiteDomainVariable.getCurrentAllowableValues().cardinality()];

		int j = 0;
		for (int i = finiteDomainVariable.getCurrentAllowableValues().nextSetBit(0); i != -1; i = finiteDomainVariable.getCurrentAllowableValues().nextSetBit(i + 1)) {
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
			BitSet bs = new BitSet(finiteDomainVariable.getCurrentAllowableValues().size());
			bs.set(indices[k++]);

			if (finiteDomainVariable.trySetValue(bs) && solver.resolveConstraints()) {
				return true;
			}

			solver.restore(mark);
		}

		return false;
	}
}
