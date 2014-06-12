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

import java.util.Stack;

/**
 * A backtracking stack of domain values mapped to variables.
 *
 * @author Eric Fritz
 */
final class Trail
{
	/**
	 * The stack of domain values.
	 */
	private Stack<VarState> stack = new Stack<>();

	/**
	 * Returns the size of the current stack.
	 * <p/>
	 * This method is meant to be used in conjunction with <tt>restore</tt>.
	 *
	 * @return The size of the current stack.
	 */
	public int size() {
		return stack.size();
	}

	/**
	 * Saves the current domain of a variable on the stack.
	 *
	 * @param variable The variable.
	 */
	public <T extends Domain> void save(Variable<T> variable) {
		stack.push(new VarState(variable, variable.getDomain()));
	}

	/**
	 * Unwinds the stack, restoring all changes to domains since <tt>mark</tt>.
	 *
	 * @param mark The number of times to unwind the stack.
	 */
	public void restore(int mark) {
		while (stack.size() > mark) {
			stack.pop().restore();
		}
	}

	/**
	 * A variable/domain pair.
	 */
	private class VarState<T extends Domain>
	{
		/**
		 * The variable.
		 */
		private Variable<T> variable;

		/**
		 * The domain.
		 */
		private T domain;

		/**
		 * Creates a new VarState.
		 *
		 * @param variable The variable.
		 * @param domain   The domain.
		 */
		public VarState(Variable<T> variable, T domain) {
			this.variable = variable;
			this.domain = domain;
		}

		/**
		 * Restores the variable's old domain value.
		 */
		public void restore() {
			variable.setDomain(domain);
		}
	}
}
