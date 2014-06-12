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
 * Represents a node in the constraint network.
 *
 * @author Eric Fritz
 */
public class Variable<T extends Domain>
{
	/**
	 * The solver.
	 */
	private Solver solver;

	/**
	 * The variable's current domain.
	 */
	private T domain;

	/**
	 * Creates a new Variable.
	 *
	 * @param solver The solver.
	 * @param domain The current domain.
	 */
	public Variable(Solver solver, T domain) {
		this.solver = solver;
		this.domain = domain;

		solver.addVariable(this);
	}

	/**
	 * Returns the solver.
	 *
	 * @return The solver.
	 */
	public Solver getSolver() {
		return solver;
	}

	/**
	 * Returns the current domain.
	 *
	 * @return The current domain.
	 */
	public T getDomain() {
		return domain;
	}

	/**
	 * Updates the current domain.
	 *
	 * @param domain The domain.
	 */
	public void setDomain(T domain) {
		this.domain = domain;
	}

	/**
	 * Updates the current domain if the assignment is consistent with the network.
	 *
	 * @param domain The domain.
	 *
	 * @return <tt>true</tt> if the assignment is consistent, <tt>false</tt> otherwise,.
	 */
	public boolean trySetValue(T domain) {
		return solver.trySetValue(this, domain);
	}
}
