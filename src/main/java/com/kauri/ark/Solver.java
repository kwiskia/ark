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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Solver
 *
 * @author Eric Fritz
 */
public class Solver
{
	private List<Variable<?>> variables = new ArrayList<>();

	private Queue<Arc> worklist = new LinkedList<>();
	private Stack<VarState<?>> stack = new Stack<>();
	private boolean solving = false;

	public void addVariable(Variable variable) {
		variables.add(variable);
	}

	public void solve(SolutionHandler handler) {
		if (solving) {
			throw new RuntimeException("Already solving.");
		}

		if (variables.isEmpty()) {
			return;
		}

		for (Variable v : variables) {
			if (v.isEmpty()) {
				return;
			}
		}

		solving = true;

		Stack<ValueEnumerator> values = new Stack<>();
		values.push(variables.get(0).getUniqueValues());

		while (!values.isEmpty()) {
			if (values.lastElement().advance()) {
				if (values.size() >= variables.size()) {
					if (!handler.handle()) {
						break;
					}
				} else {
					values.push(variables.get(values.size()).getUniqueValues());
				}
			} else {
				values.pop();
			}
		}

		solving = false;
	}

	protected boolean resolveConstraints() {
		while (!worklist.isEmpty()) {
			Arc arc = worklist.poll();

			if (!arc.update(this)) {
				worklist.clear();
				return false;
			}
		}

		return true;
	}

	protected void queue(Arc arc) {
		if (!worklist.contains(arc)) {
			worklist.add(arc);
		}
	}

	protected int saveValues() {
		return stack.size();
	}

	protected <T> void saveValue(Variable<T> variable, T allowableValues) {
		stack.push(new VarState(variable, allowableValues));
	}

	protected void restore(int mark) {
		while (stack.size() > mark) {
			stack.pop().restore();
		}

		worklist.clear();
	}

	private class VarState<T>
	{
		private Variable<T> variable;
		private T allowableValues;

		public VarState(Variable<T> variable, T allowableValues) {
			this.variable = variable;
			this.allowableValues = allowableValues;
		}

		public void restore() {
			variable.allowableValues = allowableValues;
		}
	}
}
