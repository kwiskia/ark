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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
	private Map<Variable, List<Arc>> neighbors = new HashMap<>();

	private Queue<Arc> worklist = new LinkedList<>();
	private boolean solving = false;

	private Trail trail = new Trail();

	public <T extends Variable> void addVariable(T variable) {
		variables.add(variable);
		neighbors.put(variable, new ArrayList<Arc>());
	}

	public <T extends Variable> void addConstraint(Constraint<T> constraint, T... variables) {
		for (T variable1 : variables) {
			if (!this.variables.contains(variable1)) {
				throw new RuntimeException("Adding constraint on non-registered variable.");
			}
		}

		for (T variable1 : variables) {
			Arc<T> arc = new Arc(variable1, constraint);

			for (T variable2 : variables) {
				if (variable1 != variable2) {
					neighbors.get(variable2).add(arc);
				}
			}
		}
	}

	public void solve(SolutionHandler handler) {
		if (variables.isEmpty()) {
			return;
		}

		for (Variable v : variables) {
			if (v.getDomain().isEmpty()) {
				return;
			}
		}

		if (solving) {
			throw new RuntimeException("Already solving.");
		}

		solving = true;

		Stack<ValueAdvancer> values = new Stack<>();

		Variable v = variables.get(0);
		values.push(new ValueAdvancer(v, trail.size()));

		while (!values.isEmpty()) {
			if (values.lastElement().advance()) {
				if (values.size() >= variables.size()) {
					Solution solution = new Solution();

					for (Variable variable : variables) {
						solution.add(variable);
					}

					if (!handler.handle(solution)) {
						break;
					}
				} else {
					v = variables.get(values.size());
					values.push(new ValueAdvancer(v, trail.size()));
				}
			} else {
				values.pop();
			}
		}

		solving = false;
	}

	public <T extends Domain> boolean trySetValue(Variable<T> variable, T domain) {
		if (!variables.contains(variable)) {
			throw new RuntimeException("Setting assignment on non-registered variable.");
		}

		if (domain.isEmpty()) {
			return false;
		}

		if (!variable.getDomain().equals(domain)) {
			trail.save(variable, variable.getDomain());
			variable.setDomain(domain);
			queueNeighboringArcs(variable);
		}

		return true;
	}

	private <T extends Variable> void queueNeighboringArcs(T variable) {
		for (Arc<T> arc : neighbors.get(variable)) {
			if (!worklist.contains(arc)) {
				worklist.add(arc);
			}
		}
	}

	private boolean resolveConstraints() {
		while (!worklist.isEmpty()) {
			if (!worklist.poll().update()) {
				worklist.clear();
				return false;
			}
		}

		return true;
	}

	private class ValueAdvancer<T extends Domain>
	{
		private Variable<T> variable;
		private UniqueValueIterator<T> enumerator;
		private boolean hasAdvanced = false;
		private int mark;

		public ValueAdvancer(Variable<T> variable, int mark) {
			this.variable = variable;
			this.enumerator = variable.getDomain().getUniqueValues();
			this.mark = mark;
		}

		public boolean advance() {
			if (hasAdvanced) {
				trail.restore(mark);
			}

			while (true) {
				T value = enumerator.next();

				if (value == null) {
					return false;
				}

				if (trySetValue(variable, value) && resolveConstraints()) {
					hasAdvanced = true;
					return true;
				}

				trail.restore(mark);
			}
		}
	}
}
