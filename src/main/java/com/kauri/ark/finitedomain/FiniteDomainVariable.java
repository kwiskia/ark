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

package com.kauri.ark.finitedomain;

import com.kauri.ark.Solver;
import com.kauri.ark.Variable;
import com.kauri.ark.integer.IntegerVariable;
import java.util.Arrays;
import java.util.List;

/**
 * A variable with a <tt>FiniteDomain</tt> parameterized by <tt>T</tt>.
 *
 * @author Eric Fritz
 */
public class FiniteDomainVariable<T> extends Variable<FiniteDomain<T>>
{
	/**
	 * Creates a new FiniteDomainVariable.
	 *
	 * @param solver   The solver.
	 * @param elements The elements which form the finite domain.
	 */
	public FiniteDomainVariable(Solver solver, T... elements) {
		this(solver, Arrays.asList(elements));
	}

	/**
	 * Creates a new FiniteDomainVariable.
	 *
	 * @param solver   The solver.
	 * @param elements The elements which form the finite domain.
	 */
	public FiniteDomainVariable(Solver solver, List<T> elements) {
		this(solver, new FiniteDomain<>(elements));
	}

	/**
	 * Creates a new FiniteDomainVariable.
	 *
	 * @param solver The solver.
	 * @param domain The domain.
	 */
	public FiniteDomainVariable(Solver solver, FiniteDomain<T> domain) {
		super(solver, domain);
	}

	/**
	 * Creates a series of constraints so that each supplied variable have equivalent values.
	 *
	 * @param variables The set of variables to constrain.
	 */
	public static <T> void allSame(FiniteDomainVariable<T>... variables) {
		for (int i = 0; i < variables.length - 1; i++) {
			FiniteDomainVariable<T> var1 = variables[i];
			FiniteDomainVariable<T> var2 = variables[i + 1];

			variables[i].getSolver().addConstraint(new FiniteDomainEqualityConstraint(var1, var2), var1, var2);
		}
	}

	/**
	 * Creates a series of constraints so that each supplied variable have distinct values.
	 *
	 * @param variables The set of variables to constrain.
	 */
	public static <T> void allDiff(FiniteDomainVariable<T>... variables) {
		for (int i = 0; i < variables.length - 1; i++) {
			for (int j = i + 1; j < variables.length; j++) {
				FiniteDomainVariable<T> var1 = variables[i];
				FiniteDomainVariable<T> var2 = variables[j];

				variables[i].getSolver().addConstraint(new FiniteDomainInequalityConstraint(var1, var2), var1, var2);
			}
		}
	}

	/**
	 * Creates a variable which counts the occurrences of <tt>value</tt> in the supplied variables.
	 *
	 * @param value     The target value.
	 * @param variables The set of variables.
	 *
	 * @return The counter variable.
	 */
	public static <T> IntegerVariable cardinality(T value, FiniteDomainVariable<T>... variables) {
		IntegerVariable v = new IntegerVariable(variables[0].getSolver());

		Variable[] vars = new Variable[variables.length + 1];

		vars[vars.length - 1] = v;
		for (int i = 0; i < variables.length; i++) {
			vars[i] = variables[i];
		}

		variables[0].getSolver().addConstraint(new FiniteDomainCardinalityConstraint<>(value, v, variables), vars);
		return v;
	}

	/**
	 * Creates a constraint forcing the number of occurrences of <tt>value</tt> in the supplied variables to be
	 * at least <tt>lower</tt>.
	 *
	 * @param value     The target value.
	 * @param lower     The lower bound of occurrences.
	 * @param variables The set of variables.
	 */
	public static <T> void atLeast(T value, int lower, FiniteDomainVariable<T>... variables) {
		cardinality(value, variables).ge(lower);
	}

	/**
	 * Creates a constraint forcing the number of occurrences of <tt>value</tt> in the supplied variables to be
	 * at most <tt>upper</tt>.
	 *
	 * @param value     The target value.
	 * @param upper     The upper bound of occurrences.
	 * @param variables The set of variables.
	 */
	public static <T> void atMost(T value, int upper, FiniteDomainVariable<T>... variables) {
		cardinality(value, variables).le(upper);
	}

	/**
	 * Creates a constraint forcing the number of occurrences of <tt>value</tt> in the supplied variables to be
	 * between <tt>lower</tt> and <tt>upper</tt>.
	 *
	 * @param value     The target value.
	 * @param lower     The lower bound of occurrences.
	 * @param upper     The upper bound of occurrences.
	 * @param variables The set of variables.
	 */
	public static <T> void between(T value, int lower, int upper, FiniteDomainVariable<T>... variables) {
		IntegerVariable v = cardinality(value, variables);
		v.ge(lower);
		v.le(upper);
	}

	/**
	 * Creates a constraint forcing this variable to be equivalent to <tt>value</tt>.
	 *
	 * @param value The value to be equal to.
	 *
	 * @return <tt>this</tt>
	 */
	public FiniteDomainVariable<T> eq(T value) {
		return eq(new FiniteDomainVariable<>(getSolver(), getDomain().retain(value)));
	}

	/**
	 * Creates a constraint forcing this variable to be equivalent to <tt>variable</tt>.
	 *
	 * @param variable The variable to be equal to.
	 *
	 * @return <tt>this</tt>
	 */
	public FiniteDomainVariable<T> eq(FiniteDomainVariable<T> variable) {
		getSolver().addConstraint(new FiniteDomainEqualityConstraint<>(this, variable), this, variable);
		return this;
	}

	/**
	 * Creates a constraint forcing this variable to be distinct from <tt>value</tt>.
	 *
	 * @param value The value to be distinct from.
	 *
	 * @return <tt>this</tt>
	 */
	public FiniteDomainVariable<T> ne(T value) {
		return ne(new FiniteDomainVariable<>(getSolver(), getDomain().remove(value)));
	}

	/**
	 * Creates a constraint forcing this variable to be distinct from <tt>variable</tt>.
	 *
	 * @param variable The variable to be distinct from.
	 *
	 * @return <tt>this</tt>
	 */
	public FiniteDomainVariable<T> ne(FiniteDomainVariable<T> variable) {
		getSolver().addConstraint(new FiniteDomainInequalityConstraint<>(this, variable), this, variable);
		return this;
	}

	/**
	 * Creates a mapping constraint between this variable and <tt>variable</tt>.
	 *
	 * @param variable The variable to map.
	 * @param mapping  The mapping.
	 *
	 * @return <tt>this</tt>
	 */
	public <T2> FiniteDomainVariable<T> map(FiniteDomainVariable<T2> variable, Mapping<T, T2> mapping) {
		getSolver().addConstraint(new FiniteDomainMappingConstraint<>(this, variable, mapping), (Variable) this, variable);
		return this;
	}
}
