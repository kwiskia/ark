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
 * FiniteDomainVariable
 *
 * @author Eric Fritz
 */
public class FiniteDomainVariable<T> extends Variable<FiniteDomain<T>>
{
	public FiniteDomainVariable(Solver solver, T... elements) {
		this(solver, Arrays.asList(elements));
	}

	public FiniteDomainVariable(Solver solver, List<T> elements) {
		this(solver, new FiniteDomain<>(elements));
	}

	public FiniteDomainVariable(Solver solver, FiniteDomain<T> domain) {
		super(solver, domain);
	}

	//
	// Static constraint helpers

	public static <T> void allSame(FiniteDomainVariable<T>... variables) {
		variables[0].getSolver().addConstraint(new FiniteDomainEqualityConstraint<>(variables), variables);
	}

	public static <T> void allDiff(FiniteDomainVariable<T>... variables) {
		variables[0].getSolver().addConstraint(new FiniteDomainInequalityConstraint<>(variables), variables);
	}

	public static <T>IntegerVariable cardinality(T value, FiniteDomainVariable<T>... variables) {
		IntegerVariable v = new IntegerVariable(variables[0].getSolver());

		Variable[] vars = new Variable[variables.length + 1];

		vars[vars.length - 1] = v;
		for (int i = 0; i < variables.length; i++) {
			vars[i] = variables[i];
		}

		variables[0].getSolver().addConstraint(new FiniteDomainCardinalityConstraint<>(value, v, variables), vars);
		return v;
	}

	public static <T> void atLeast(T value, int lower, FiniteDomainVariable<T>... variables) {
		cardinality(value, variables).ge(lower);
	}

	public static <T> void atMost(T value, int upper, FiniteDomainVariable<T>... variables) {
		cardinality(value, variables).le(upper);
	}

	public static <T> void between(T value, int lower, int upper, FiniteDomainVariable<T>... variables) {
		IntegerVariable v = cardinality(value, variables);
		v.ge(lower);
		v.le(upper);
	}

	//
	// Constraint helpers

	public FiniteDomainVariable<T> eq(T value) {
		return eq(new FiniteDomainVariable<>(getSolver(), getDomain().retain(value)));
	}

	public FiniteDomainVariable<T> eq(FiniteDomainVariable<T> variable) {
		getSolver().addConstraint(new FiniteDomainEqualityConstraint<>(this, variable), this, variable);
		return this;
	}

	public FiniteDomainVariable<T> ne(T value) {
		return ne(new FiniteDomainVariable<>(getSolver(), getDomain().remove(value)));
	}

	public FiniteDomainVariable<T> ne(FiniteDomainVariable<T> variable) {
		getSolver().addConstraint(new FiniteDomainInequalityConstraint<>(this, variable), this, variable);
		return this;
	}

	public <T2> FiniteDomainVariable<T> map(FiniteDomainVariable<T2> variable, Mapping<T, T2> mapping) {
		getSolver().addConstraint(new FiniteDomainMappingConstraint<>(this, variable, mapping), (Variable) this, variable);
		return this;
	}
}
