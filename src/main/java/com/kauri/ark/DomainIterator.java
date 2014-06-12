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

import java.util.Iterator;

/**
 * An iterator over a domain parameterized by <tt>T</tt>.
 * <p/>
 * This iterator is expected to iterate over (eventually) unique values of the domain. However, the iterator may return
 * a (proper) subset of the entire domain. If that subset of the domain is consistent with the constraint network, then
 * the consumer should call the <tt>lastDomainValid</tt> method to inform the iterator.
 * <p/>
 * If the <tt>lastDomainValid</tt> method is called, the iterator must iterate over values of the most recently returned
 * domain before iteration completes. If the <tt>lastDomainValid</tt> method is not called, the iterator is not expected
 * to iterate over any value in that subset (as none of them will be a valid assignment).
 *
 * @author Eric Fritz
 */
public interface DomainIterator<T> extends Iterator<Domain<T>>
{
	/**
	 * Signals to the iterator that the last domain was consistent with the constraint network.
	 */
	public void lastDomainValid();
}
