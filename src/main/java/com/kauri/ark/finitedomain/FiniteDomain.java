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

import com.kauri.ark.Domain;
import com.kauri.ark.UniqueValueIterator;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * TempFiniteDomain
 *
 * @author Eric Fritz
 */
public class FiniteDomain<T> implements Domain<T>, Iterable<T>
{
	private List<T> elements;
	private BitSet bitset;

	public FiniteDomain(List<T> elements) {
		this(elements, null);
	}

	public FiniteDomain(List<T> elements, BitSet bitset) {
		if (bitset == null) {
			bitset = new BitSet(elements.size());
			bitset.set(0, elements.size());
		}

		this.elements = elements;
		this.bitset = bitset;
	}

	@Override
	public boolean isEmpty() {
		return bitset.cardinality() == 0;
	}

	@Override
	public boolean isUnique() {
		return bitset.cardinality() == 1;
	}

	public <T2> FiniteDomain<T> mapForward(FiniteDomain<T2> domain, Mapping<T2, T> mapping) {
		BitSet newSet = new BitSet();

		for (T2 element : domain) {
			newSet.set(elements.indexOf(mapping.getForwardMapping(element)));
		}

		return new FiniteDomain<>(elements, newSet);
	}

	public <T2> FiniteDomain<T> mapReverse(FiniteDomain<T2> domain, Mapping<T, T2> mapping) {
		BitSet newSet = new BitSet();

		for (T2 element : domain) {
			newSet.set(elements.indexOf(mapping.getReverseMapping(element)));
		}

		return new FiniteDomain<>(elements, newSet);
	}

	public boolean contains(T element) {
		if (!elements.contains(element)) {
			throw new RuntimeException("Element does not belong to finite domain.");
		}

		return bitset.get(elements.indexOf(element));
	}

	public FiniteDomain<T> retain(T element) {
		if (!elements.contains(element)) {
			throw new RuntimeException("Element does not belong to finite domain.");
		}

		BitSet newSet = new BitSet(bitset.size());
		newSet.set(elements.indexOf(element));
		return new FiniteDomain<>(elements, newSet);
	}

	public FiniteDomain<T> remove(T element) {
		if (!elements.contains(element)) {
			throw new RuntimeException("Element does not belong to finite domain.");
		}

		BitSet newSet = bitset.get(0, bitset.size());
		newSet.clear(elements.indexOf(element));
		return new FiniteDomain<>(elements, newSet);
	}

	public FiniteDomain<T> retainAll(FiniteDomain<T> other) {
		if (!elements.equals(other.elements)) {
			throw new RuntimeException("Finite domains do not match.");
		}

		BitSet newSet = bitset.get(0, bitset.size());
		newSet.and(other.bitset);
		return new FiniteDomain<>(elements, newSet);
	}

	public FiniteDomain<T> removeAll(FiniteDomain<T> other) {
		if (!elements.equals(other.elements)) {
			throw new RuntimeException("Finite domains do not match.");
		}

		BitSet newSet = bitset.get(0, bitset.size());
		newSet.andNot(other.bitset);
		return new FiniteDomain<>(elements, newSet);
	}

	public T getUniqueValue() {
		if (!isUnique()) {
			throw new RuntimeException("Domain has not been narrowed to a unique value.");
		}

		return elements.get(bitset.nextSetBit(0));
	}

	@Override
	public UniqueValueIterator<Domain<T>> getUniqueValues() {
		return new FiniteDomainValueEnumerator(bitset);
	}

	@Override
	public Iterator<T> iterator() {
		return new ValueIterator();
	}

	private class ValueIterator implements Iterator<T>
	{
		private int k = 0;
		private int[] indices;

		public ValueIterator() {
			indices = new int[bitset.cardinality()];

			int j = 0;
			for (int i = bitset.nextSetBit(0); i >= 0; i = bitset.nextSetBit(i + 1)) {
				indices[j++] = i;
			}
		}

		@Override
		public boolean hasNext() {
			return k < indices.length;
		}

		@Override
		public T next() {
			if (k >= indices.length) {
				throw new NoSuchElementException();
			}

			return elements.get(indices[k++]);
		}
	}

	private class FiniteDomainValueEnumerator implements UniqueValueIterator<Domain<T>>
	{
		private BitSet bitset;
		private int k = -1;

		public FiniteDomainValueEnumerator(BitSet bitset) {
			this.bitset = bitset;
		}

		@Override
		public FiniteDomain<T> next() {
			k = bitset.nextSetBit(k + 1);

			if (k == -1) {
				return null;
			}

			BitSet bs = new BitSet(bitset.size());
			bs.set(k);
			return new FiniteDomain<>(elements, bs);
		}
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof FiniteDomain)) {
			return false;
		}

		return elements.equals(((FiniteDomain) o).elements) && bitset.equals(((FiniteDomain) o).bitset);
	}
}
