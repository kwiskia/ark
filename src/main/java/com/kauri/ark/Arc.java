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
 * A variable/constraint pair.
 *
 * @author Eric Fritz
 */
final class Arc<T extends Domain>
{
	/**
	 * The variable.
	 */
	private Variable<T> variable;

	/**
	 * The constraint.
	 */
	private Constraint<T> constraint;

	/**
	 * Creates a new Arc.
	 *
	 * @param variable   The variable.
	 * @param constraint The constraint.
	 */
	public Arc(Variable<T> variable, Constraint<T> constraint) {
		this.variable = variable;
		this.constraint = constraint;
	}

	/**
	 * Calls to <tt>constraint</tt> to narrow the domain of <tt>variable</tt>.
	 *
	 * @return <tt>true</tt> if the variable remains arc-consistent with its network.
	 */
	public boolean update() {
		return constraint.narrow(variable);
	}
}
