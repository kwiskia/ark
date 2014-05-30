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

import com.kauri.ark.ValueEnumerator;
import java.util.BitSet;

/**
 * FiniteDomainValueEnumerator
 *
 * @author Eric Fritz
 */
class FiniteDomainValueEnumerator<T> implements ValueEnumerator<BitSet>
{
	private int size;
	private int k = 0;
	private int[] indices;

	public FiniteDomainValueEnumerator(FiniteDomainVariable<T> variable) {
		size = variable.getAllowableValues().size();
		indices = new int[variable.getCurrentAllowableValues().cardinality()];

		int j = 0;
		for (int i = variable.getCurrentAllowableValues().nextSetBit(0); i != -1; i = variable.getCurrentAllowableValues().nextSetBit(i + 1)) {
			indices[j++] = i;
		}
	}

	@Override
	public BitSet next() {
		if (k < indices.length) {
			BitSet bs = new BitSet(size);
			bs.set(indices[k++]);
			return bs;
		}

		return null;
	}
}
