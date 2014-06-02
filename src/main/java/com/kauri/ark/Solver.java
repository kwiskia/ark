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

	public <T extends Domain> void addVariable(Variable<T> variable) {
		variables.add(variable);
		neighbors.put(variable, new ArrayList<Arc>());
	}

	public <T extends Domain> void addConstraint(Constraint<T> constraint, Variable<T>... variables) {
		for (Variable<T> variable1 : variables) {
			if (!this.variables.contains(variable1)) {
				throw new RuntimeException("Adding constraint on non-registered variable.");
			}
		}

		for (Variable<T> variable1 : variables) {
			Arc<T> arc = new Arc(variable1, constraint);

			for (Variable<T> variable2 : variables) {
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
		solveRecursive(handler, new Stack<Variable>());
		solving = false;
	}

	private void solveRecursive(SolutionHandler handler, Stack<Variable> selected) {
		if (!solving) {
			return;
		}

		if (selected.size() == variables.size()) {
			Solution solution = new Solution();

			for (Variable variable : variables) {
				solution.add(variable);
			}

			if (!handler.handle(solution)) {
				solving = false;
			}

			return;
		}

		int mark = trail.size();
		Variable v = getMostConstrainedVariable(selected);
		selected.add(v);
		DomainIterator<? extends Domain> iterator = v.getDomain().getUniqueValues();

		while (iterator.hasNext() && solving) {
			Domain value = iterator.next();

			if (trySetValue(v, value) && resolveConstraints()) {
				iterator.lastDomainValid();

				if (value.isUnique()) {
					solveRecursive(handler, selected);
				}
			}

			trail.restore(mark);
		}

		selected.remove(v);
	}

	private Variable getMostConstrainedVariable(Stack<Variable> selected) {
		Variable v1 = null;

		for (Variable v2 : variables) {
			if (selected.contains(v2)) {
				continue;
			}

			if (v1 == null || v2.getDomain().size() < v1.getDomain().size()) {
				v1 = v2;
			}
		}

		return v1;
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

	private <T extends Domain> void queueNeighboringArcs(Variable<T> variable) {
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
}
