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
	public enum ExpansionOrder
	{
		DETERMINISTIC,
		RANDOM
	}

	private List<Variable<?>> variables = new ArrayList<>();
	private Map<Constraint, List<Arc>> arcs = new HashMap<>();
	private Map<Variable, List<Constraint>> edges = new HashMap<>();

	private ExpansionOrder order = ExpansionOrder.DETERMINISTIC;

	private Queue<Arc> worklist = new LinkedList<>();
	private Stack<VarState<?>> stack = new Stack<>();
	private boolean solving = false;

	public void addVariable(Variable variable) {
		variables.add(variable);
		edges.put(variable, new ArrayList<>());
	}

	public <T> void addConstraint(Constraint<?> constraint, Variable<T>... variables) {
		arcs.put(constraint, new ArrayList<>());

		for (Variable<T> variable : variables) {
			if (!this.variables.contains(variable)) {
				throw new RuntimeException("Adding constraint on non-registered variable.");
			}

			edges.get(variable).add(constraint);
			arcs.get(constraint).add(new Arc(variable, constraint));
		}
	}

	public <T> void queueNeighboringArcs(Variable<T> variable) {
		for (Constraint constraint : edges.get(variable)) {
			for (Arc arc : arcs.get(constraint)) {
				if (arc.variable != variable) {
					if (!worklist.contains(arc)) {
						worklist.add(arc);
					}
				}
			}
		}
	}

	public ExpansionOrder getExpansionOrder() {
		return order;
	}

	public void setExpansionOrder(ExpansionOrder order) {
		this.order = order;
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
		values.push(variables.get(0).getValueEnumerator());

		while (!values.isEmpty()) {
			if (values.lastElement().advance()) {
				if (values.size() >= variables.size()) {
					if (!handler.handle()) {
						break;
					}
				} else {
					values.push(variables.get(values.size()).getValueEnumerator());
				}
			} else {
				values.pop();
			}
		}

		solving = false;
	}

	public boolean resolveConstraints() {
		while (!worklist.isEmpty()) {
			Arc arc = worklist.poll();

			if (!arc.constraint.update(arc.variable)) {
				worklist.clear();
				return false;
			}
		}

		return true;
	}

	public int saveValues() {
		return stack.size();
	}

	public <T> void saveValue(Variable<T> variable, T allowableValues) {
		stack.push(new VarState(variable, allowableValues));
	}

	public void restore(int mark) {
		while (stack.size() > mark) {
			stack.pop().restore();
		}

		worklist.clear();
	}

	private class Arc<T extends Variable<?>>
	{
		private T variable;
		private Constraint constraint;

		public Arc(T variable, Constraint constraint) {
			this.variable = variable;
			this.constraint = constraint;
		}
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
			variable.set(allowableValues);
		}
	}
}
