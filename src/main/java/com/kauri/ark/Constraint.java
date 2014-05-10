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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Constraint
 *
 * @author Eric Fritz
 */
abstract public class Constraint<T extends Variable<?>>
{
	protected List<T> variables = new ArrayList<>();
	protected List<Arc> arcs = new ArrayList<>();

	public Constraint(T... variables) {
		this.variables = Arrays.asList(variables);

		for (T t : variables) {
			arcs.add(new Arc<>(t, this));
			t.constraints.add(this);
		}
	}

	public void queueAllArcs(Solver solver) {
		for (Arc arc : arcs) {
			arc.markForUpdate(solver);
		}
	}

	public boolean narrowed(Solver solver, T variable) {
		for (Arc arc : arcs) {
			if (arc.getVariable().isEmpty()) {
				return false;
			}

			if (arc.getVariable() != variable) {
				arc.markForUpdate(solver);
			}
		}

		return true;
	}

	abstract public boolean updateVariable(Solver solver, T variable);
}
