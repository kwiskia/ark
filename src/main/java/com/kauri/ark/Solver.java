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
import java.util.Iterator;
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
	abstract public static class Constraint<T extends Variable<?>>
	{
		protected List<T> variables = new ArrayList<>();
		protected List<Arc> arcs = new ArrayList<>();

		public Constraint(T... variables) {
			this.variables = Arrays.asList(variables);

			for (T t : variables) {
				arcs.add(new Arc<T>(t, this));
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
				if (arc.variable.isEmpty()) {
					return false;
				}

				if (arc.variable != variable) {
					arc.markForUpdate(solver);
				}
			}

			return true;
		}

		abstract public boolean updateVariable(Solver solver, T variable);
	}

	public static class EqualityConstraint<T> extends Constraint<FiniteDomainVariable<T>>
	{
		private FiniteDomainVariable<T> var1;
		private FiniteDomainVariable<T> var2;

		public EqualityConstraint(FiniteDomainVariable<T> var1, FiniteDomainVariable<T> var2) {
			super(var1, var2);
			this.var1 = var1;
			this.var2 = var2;
		}

		public boolean updateVariable(Solver solver, FiniteDomainVariable<T> variable) {
			FiniteDomainVariable<T> other = variable == var1 ? var2 : var1;

			if (other.isUnique()) {
				return variable.trySetValue(solver, other.allowableValues);
			}

			return true;
		}
	}

	public static class InequalityConstraint<T> extends Constraint<FiniteDomainVariable<T>>
	{
		private FiniteDomainVariable<T> var1;
		private FiniteDomainVariable<T> var2;

		public InequalityConstraint(FiniteDomainVariable<T> var1, FiniteDomainVariable<T> var2) {
			super(var1, var2);
			this.var1 = var1;
			this.var2 = var2;
		}

		public boolean updateVariable(Solver solver, FiniteDomainVariable<T> variable) {
			FiniteDomainVariable<T> other = variable == var1 ? var2 : var1;

			if (other.isUnique()) {
				List<T> values = new ArrayList<>(variable.allowableValues);
				values.removeAll(other.allowableValues);

				return variable.trySetValue(solver, values);
			}

			return true;
		}
	}

	public static class CardinalityConstraint<T> extends Constraint<FiniteDomainVariable<T>>
	{
		private T value;
		private int min;
		private int max;

		public CardinalityConstraint(T value, int min, int max, FiniteDomainVariable<T>... vars) {
			super(vars);

			this.value = value;
			this.min = min;
			this.max = max;
		}

		public boolean updateVariable(Solver solver, FiniteDomainVariable<T> variable) {
			int possible = 0;
			int definite = 0;

			for (FiniteDomainVariable<T> v : variables) {
				if (v.allowableValues.contains(value)) {
					possible++;

					if (v.isUnique()) {
						definite++;
					}
				}
			}

			if (possible < min || definite > max) {
				return false;
			}

			if (definite == max) {
				for (FiniteDomainVariable<T> v : variables) {
					if (v.allowableValues.contains(value) && !v.isUnique()) {
						List<T> values = new ArrayList<>(v.allowableValues);
						values.remove(value);

						if (!v.trySetValue(solver, values)) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	abstract public static class Variable<T>
	{
		protected String name;
		protected T allowableValues;
		protected List<Constraint> constraints = new ArrayList<>();

		public Variable(String name, T allowableValues) {
			this.name = name;
			this.allowableValues = allowableValues;
		}

		public String toString() {
			return name;
		}

		abstract public Iterator<T> getUniqueValues(Solver solver);

		abstract public boolean isEmpty();

		abstract public boolean isUnique();

		abstract public Object getUniqueValue();

		protected boolean trySetAndResolveConstraints(Solver solver, T value) {
			return trySetValue(solver, value) && solver.resolveConstraints();
		}

		protected boolean trySetValue(Solver solver, T value) {
			if (!allowableValues.equals(value)) {
				solver.saveValue(this, allowableValues);
				allowableValues = value;

				if (!narrowConstraints(solver)) {
					return false;
				}
			}

			return !isEmpty();
		}

		protected boolean narrowConstraints(Solver solver) {
			for (Constraint c : constraints) {
				if (!c.narrowed(solver, this)) {
					return false;
				}
			}

			return true;
		}
	}

	public static class FiniteDomainVariable<T> extends Variable<List<T>>
	{
		public FiniteDomainVariable(String name, List<T> allowableValues) {
			super(name, allowableValues);
		}

		public Iterator<List<T>> getUniqueValues(Solver solver) {
			return new FiniteDomainIterator(solver, solver.saveValues());
		}

		public boolean isEmpty() {
			return allowableValues.size() == 0;
		}

		public boolean isUnique() {
			return allowableValues.size() == 1;
		}

		public T getUniqueValue() {
			if (!isUnique()) {
				throw new RuntimeException("Not unique.");
			}

			return allowableValues.get(0);
		}

		private class FiniteDomainIterator implements Iterator<List<T>>
		{
			private Solver solver;
			private int mark;
			private Iterator<T> values;
			private boolean isUnique;
			private boolean wasUnique = false;

			public FiniteDomainIterator(Solver solver, int mark) {
				this.solver = solver;
				this.mark = mark;
				this.values = allowableValues.iterator();
			}

			@Override
			public boolean hasNext() {
				if (wasUnique) {
					solver.restore(mark);
					return false;
				}

				if (!isUnique) {
					solver.restore(mark);
				}

				isUnique = isUnique();

				if (isUnique()) {
					if (!narrowConstraints(solver) || !solver.resolveConstraints()) {
						solver.restore(mark);
						return false;
					}

					wasUnique = true;
					return true;
				} else {
					while (values.hasNext()) {
						if (trySetAndResolveConstraints(solver, Arrays.asList(values.next()))) {
							return true;
						}
					}
				}

				return false;
			}

			@Override
			public List<T> next() {
				return allowableValues;
			}
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
			variable.allowableValues = allowableValues;
		}
	}

	private Stack<VarState<?>> stack = new Stack<>();

	private static class Arc<T extends Variable<?>>
	{
		private T variable;
		private Constraint constraint;
		public boolean queuedForUpdate = false;

		public Arc(T variable, Constraint constraint) {
			this.variable = variable;
			this.constraint = constraint;
		}

		public boolean update(Solver solver) {
			return constraint.updateVariable(solver, variable);
		}

		public void markForUpdate(Solver solver) {
			if (!queuedForUpdate) {
				solver.queue(this);
			}
		}
	}

	Queue<Arc> worklist = new LinkedList<>();

	private boolean resolveConstraints() {
		while (!worklist.isEmpty()) {
			Arc arc = worklist.poll();
			arc.queuedForUpdate = false;

			if (!arc.update(this)) {
				while (!worklist.isEmpty()) {
					worklist.poll().queuedForUpdate = false;
				}

				return false;
			}
		}

		return true;
	}

	public void queue(Arc arc) {
		arc.queuedForUpdate = true;
		worklist.add(arc);
	}

	private int saveValues() {
		return stack.size();
	}

	private <T> void saveValue(Variable<T> variable, T allowableValues) {
		stack.push(new VarState(variable, allowableValues));
	}

	private void restore(int mark) {
		while (stack.size() > mark) {
			stack.pop().restore();
		}

		while (!worklist.isEmpty()) {
			worklist.poll().queuedForUpdate = false;
		}
	}

	private List<Variable<?>> variables = new ArrayList<>();
	private List<Constraint<?>> constraints = new ArrayList<>();

	public void addVariable(Variable variable) {
		variables.add(variable);
	}

	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
	}

	public void solve() {
		Stack<Iterator<?>> enumerators = new Stack<>();
		enumerators.push(variables.get(0).getUniqueValues(this));

		int solution = 0;

		while (enumerators.size() > 0) {
			if (enumerators.lastElement().hasNext()) {
				enumerators.lastElement().next();

				if (enumerators.size() >= variables.size()) {
					System.out.println("Solution #" + ++solution);

					for (Variable v : variables) {
						System.out.println(v + ": " + v.allowableValues);
					}

					System.out.println();
				} else {
					enumerators.push(variables.get(enumerators.size()).getUniqueValues(this));
				}
			} else {
				enumerators.pop();
			}
		}
	}
}
