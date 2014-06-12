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
 * A constraint between two or more variables.
 *
 * @author Eric Fritz
 */
public interface Constraint<T extends Domain>
{
	/**
	 * Narrow the domain of <tt>variable</tt> so that it remains arc-consistent with its network.
	 * <p/>
	 * Constraints that narrow the domain of a variable to a smaller set will make searching more efficient, but should
	 * not narrow the domain to rule out possible solutions (or enumerating all solutions will be impossible).
	 * <p/>
	 * This method is assumed to be called only with <tt>variable</tt> arguments relevant to the constraint (the only
	 * possible arguments should be registered with <tt>Solver</tt>), and only after the domain of an adjacent variable
	 * has narrowed its domain.
	 *
	 * @param variable The variable to narrow.
	 *
	 * @return <tt>true</tt> if the variable can remain arc-consistent after narrowing, <tt>false</tt> otherwise.
	 */
	boolean narrow(Variable<T> variable);
}
