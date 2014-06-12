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
 * The constraint network solver.
 *
 * @author Eric Fritz
 */
final public class Solver
{
	/**
	 * A list of registered variables.
	 */
	private List<Variable> variables = new ArrayList<>();

	/**
	 * A map of variables to their relevant constraint arcs.
	 */
	private Map<Variable, List<Arc>> neighbors = new HashMap<>();

	/**
	 * A queue of arcs which should be updated.
	 */
	private Queue<Arc> worklist = new LinkedList<>();

	/**
	 * Whether the solver is currently looking for solutions.
	 */
	private boolean solving = false;

	/**
	 * The backtracking stack.
	 */
	private Trail trail = new Trail();

	/**
	 * Register a variable with the constraint network.
	 *
	 * @param variable The variable.
	 *
	 * @throws RuntimeException If the variable is already registered.
	 */
	public <T extends Domain> void addVariable(Variable<T> variable) {
		if (variables.contains(variable)) {
			throw new RuntimeException("Variable already registered.");
		}

		variables.add(variable);
		neighbors.put(variable, new ArrayList<Arc>());
	}

	/**
	 * Registers a constraint with the constraint network.
	 *
	 * @param constraint The constraint.
	 * @param variables  The set of constrained variables.
	 *
	 * @throws RuntimeException If one of the constrained variables has not been registered.
	 */
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

	/**
	 * Updates the current domain of a variable if the assignment is consistent with the network.
	 *
	 * @param variable The variable.
	 * @param domain   The domain.
	 *
	 * @return <tt>true</tt> if the domain is immediately consistent (non-empty), <tt>false</tt> otherwise.
	 *
	 * @throws RuntimeException If the variable has not been registered.
	 */
	public <T extends Domain> boolean trySetValue(Variable<T> variable, T domain) {
		if (!variables.contains(variable)) {
			throw new RuntimeException("Setting assignment on non-registered variable.");
		}

		if (domain.isEmpty()) {
			return false;
		}

		if (!variable.getDomain().equals(domain)) {
			trail.save(variable);
			variable.setDomain(domain);
			queueNeighboringArcs(variable);
		}

		return true;
	}

	/**
	 * Begins solving the constraint network.
	 *
	 * @param handler The solution handler.
	 *
	 * @throws RuntimeException If the solver is already looking for solutions.
	 */
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

		trail.restore(trail.size());
		solving = false;
	}

	/**
	 * The main solver routine. If all variables have a unique assignment, then the <tt>handler</tt> is called.
	 * Otherwise, a unassigned variable is chosen, and for each unique value in its current domain, the domain
	 * is assigned that value and the rest of the network is solved recursively.
	 * <p/>
	 * If the handler returns <tt>false</tt>, we immediately stop solving and rewind the stack.
	 *
	 * @param handler  The solution handler.
	 * @param selected The variables which are already assigned.
	 */
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
		selected.push(v);
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

		selected.pop();
	}

	/**
	 * Selects a variable which is not in <tt>selected</tt> with the fewest elements in its current domain.
	 *
	 * @param selected The variables which are already assigned.
	 *
	 * @return The most constrained variable.
	 */
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

	/**
	 * Adds all the arcs neighboring <tt>variable</tt> to <tt>worklist</tt>.
	 *
	 * @param variable The variable.
	 */
	private <T extends Domain> void queueNeighboringArcs(Variable<T> variable) {
		for (Arc<T> arc : neighbors.get(variable)) {
			if (!worklist.contains(arc)) {
				worklist.add(arc);
			}
		}
	}

	/**
	 * Iterates the worklist, updating each arc.
	 *
	 * @return <tt>true</tt> if the network is consistent, <tt>false</tt> otherwise.
	 */
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
