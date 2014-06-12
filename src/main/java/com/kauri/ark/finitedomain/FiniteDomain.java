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
import com.kauri.ark.DomainIterator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A finite domain represents an ordered set of elements of type <tt>T</tt>.
 *
 * @author Eric Fritz
 */
public class FiniteDomain<T> implements Domain<T>, Iterable<T>
{
	/**
	 * The elements which form the finite domain.
	 */
	private List<T> elements;

	/**
	 * A bitset indicating which elements are currently in the domain.
	 */
	private BitSet bitset;

	/**
	 * The number of elements currently in the domain.
	 */
	private int size;

	/**
	 * Creates a new FiniteDomain.
	 *
	 * @param elements The elements which form the finite domain.
	 */
	public FiniteDomain(List<T> elements) {
		this(elements, null);
	}

	/**
	 * Creates a new FiniteDomain.
	 *
	 * @param elements The elements which form the finite domain.
	 * @param bitset   A bitset indicating which elements are currently in the domain.
	 */
	public FiniteDomain(List<T> elements, BitSet bitset) {
		if (bitset == null) {
			bitset = new BitSet(elements.size());
			bitset.set(0, elements.size());
		}

		this.elements = elements;
		this.bitset = bitset;
		this.size = bitset.cardinality();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean isUnique() {
		return size == 1;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return The unique value of the domain.
	 *
	 * @throws RuntimeException If the domain is not unique.
	 */
	@Override
	public T getUniqueValue() {
		if (!isUnique()) {
			throw new RuntimeException("Domain has not been narrowed to a unique value.");
		}

		return elements.get(bitset.nextSetBit(0));
	}

	@Override
	public DomainIterator<T> getUniqueValues() {
		return new FiniteDomainIterator();
	}

	@Override
	public Iterator<T> iterator() {
		return new ValueIterator();
	}

	/**
	 * Returns a new FiniteDomain constructed by mapping each element in <tt>domain</tt> to this finite domain
	 * through the mapping <tt>mapping</tt>.
	 *
	 * @param domain  The domain.
	 * @param mapping The mapping.
	 *
	 * @return A new FiniteDomain.
	 */
	public <T2> FiniteDomain<T> mapForward(FiniteDomain<T2> domain, Mapping<T2, T> mapping) {
		BitSet newSet = new BitSet();

		for (T2 element : domain) {
			newSet.set(elements.indexOf(mapping.getForwardMapping(element)));
		}

		return new FiniteDomain<>(elements, newSet);
	}

	/**
	 * Returns a new FiniteDomain constructed by mapping each element in <tt>domain</tt> to this finite domain
	 * through the mapping <tt>mapping</tt>.
	 *
	 * @param domain  The domain.
	 * @param mapping The mapping.
	 *
	 * @return A new FiniteDomain.
	 */
	public <T2> FiniteDomain<T> mapReverse(FiniteDomain<T2> domain, Mapping<T, T2> mapping) {
		BitSet newSet = new BitSet();

		for (T2 element : domain) {
			newSet.set(elements.indexOf(mapping.getReverseMapping(element)));
		}

		return new FiniteDomain<>(elements, newSet);
	}

	/**
	 * Returns <tt>true</tt> if this domain currently contains <tt>element</tt>.
	 *
	 * @param element The element.
	 *
	 * @return <tt>true</tt> if this domain currently contains <tt>element</tt>.
	 */
	public boolean contains(T element) {
		return bitset.get(indexOf(element));
	}

	/**
	 * Returns a new FiniteDomain constructed by retaining only the element <tt>element</tt>.
	 *
	 * @param element The element.
	 *
	 * @return A new FiniteDomain constructed by retaining only the element <tt>element</tt>.
	 *
	 * @throws RuntimeException If the element is not part of the domain.
	 */
	public FiniteDomain<T> retain(T element) {
		BitSet newSet = new BitSet(bitset.size());
		newSet.set(indexOf(element));
		return new FiniteDomain<>(elements, newSet);
	}

	/**
	 * Returns a new FiniteDomain constructed by removing only the element <tt>element</tt>.
	 *
	 * @param element The element.
	 *
	 * @return A new FiniteDomain constructed by removing only the element <tt>element</tt>.
	 *
	 * @throws RuntimeException If the element is not part of the domain.
	 */
	public FiniteDomain<T> remove(T element) {
		BitSet newSet = bitset.get(0, bitset.size());
		newSet.clear(indexOf(element));
		return new FiniteDomain<>(elements, newSet);
	}

	/**
	 * Returns a new FiniteDomain constructed by retaining the elements in <tt>other</tt>.
	 *
	 * @param other Another FiniteDomain.
	 *
	 * @return A new FiniteDomain constructed by retaining the elements in <tt>other</tt>.
	 *
	 * @throws RuntimeException If the finite domains do not match.
	 */
	public FiniteDomain<T> retainAll(FiniteDomain<T> other) {
		if (!elements.equals(other.elements)) {
			throw new RuntimeException("Finite domains do not match.");
		}

		BitSet newSet = bitset.get(0, bitset.size());
		newSet.and(other.bitset);
		return new FiniteDomain<>(elements, newSet);
	}

	/**
	 * Returns a new FiniteDomain constructed by removing the elements in <tt>other</tt>.
	 *
	 * @param other Another FiniteDomain.
	 *
	 * @return A new FiniteDomain constructed by removing the elements in <tt>other</tt>.
	 *
	 * @throws RuntimeException If the finite domains do not match.
	 */
	public FiniteDomain<T> removeAll(FiniteDomain<T> other) {
		if (!elements.equals(other.elements)) {
			throw new RuntimeException("Finite domains do not match.");
		}

		BitSet newSet = bitset.get(0, bitset.size());
		newSet.andNot(other.bitset);
		return new FiniteDomain<>(elements, newSet);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof FiniteDomain)) {
			return false;
		}

		return elements.equals(((FiniteDomain) o).elements) && bitset.equals(((FiniteDomain) o).bitset);
	}

	@Override
	public String toString() {
		List<T> present = new ArrayList<>();

		for (int i = bitset.nextSetBit(0); i >= 0; i = bitset.nextSetBit(i + 1)) {
			present.add(elements.get(i));
		}

		return present.toString();
	}

	/**
	 * Returns the bit index which represents the element.
	 *
	 * @param element The element.
	 *
	 * @return The bit index which represents the element.
	 *
	 * @throws RuntimeException If the element is not part of the domain.
	 */
	private int indexOf(T element) {
		if (!elements.contains(element)) {
			throw new RuntimeException("Element does not belong to finite domain.");
		}

		return elements.indexOf(element);
	}

	//
	// TODO - document
	//

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

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private class FiniteDomainIterator implements DomainIterator<T>
	{
		private int k = 0;
		private int[] indices;

		public FiniteDomainIterator() {
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
		public FiniteDomain<T> next() {
			if (k >= indices.length) {
				throw new NoSuchElementException();
			}

			BitSet bs = new BitSet(bitset.size());
			bs.set(indices[k++]);
			return new FiniteDomain<>(elements, bs);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void lastDomainValid() {
			// last domain was singleton, nothing to narrow
		}
	}
}
