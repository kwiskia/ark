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

/**
 * Represents a set of values parameterized by <tt>T</tt>.
 *
 * @author Eric Fritz
 */
public interface Domain<T>
{
	/**
	 * Returns the number of values in the set.
	 *
	 * @return The number of values in the set.
	 */
	int size();

	/**
	 * Returns <tt>true</tt> if the domain is empty, <tt>false</tt> otherwise.
	 *
	 * @return <tt>true</tt> if the domain is empty, <tt>false</tt> otherwise.
	 */
	boolean isEmpty();

	/**
	 * Returns <tt>true</tt> if the domain has a single value, <tt>false</tt> otherwise.
	 *
	 * @return <tt>true</tt> if the domain has a single value, <tt>false</tt> otherwise.
	 */
	boolean isUnique();

	/**
	 * Returns the unique value of the domain.
	 *
	 * @return The unique value of the domain.
	 */
	T getUniqueValue();

	/**
	 * Returns a special kind of iterator over the unique values of the domain.
	 *
	 * @return A special kind of iterator over the unique values of the domain.
	 */
	DomainIterator<T> getUniqueValues();
}
