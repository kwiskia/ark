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

import java.util.BitSet;
import java.util.List;

/**
 * FiniteDomain
 *
 * @author Eric Fritz
 */
public class FiniteDomain<T>
{
	private List<T> values;

	//
	// TODO - how to ensure ordering?
	//

	public FiniteDomain(List<T> values) {
		this.values = values;
	}

	public T getValue(int index) {
		return values.get(index);
	}

	public BitSet createBitSet() {
		BitSet bs = new BitSet(values.size());
		bs.set(0, values.size());
		return bs;
	}

	public BitSet createBitSet(T... including) {
		BitSet bs = new BitSet(values.size());

		for (T t : including) {
			bs.set(values.indexOf(t));
		}

		return bs;
	}
}
