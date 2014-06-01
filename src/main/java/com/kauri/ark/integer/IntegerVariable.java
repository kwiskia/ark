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

package com.kauri.ark.integer;

import com.kauri.ark.Solver;
import com.kauri.ark.Variable;

/**
 * IntegerVariable
 *
 * @author Eric Fritz
 */
public class IntegerVariable extends Variable<IntegerDomain>
{
	public IntegerVariable(Solver solver) {
		this(solver, new IntegerDomain(new Interval(Interval.MIN_VALUE, Interval.MAX_VALUE)));
	}

	public IntegerVariable(Solver solver, int value) {
		this(solver, value, value);
	}

	public IntegerVariable(Solver solver, int lower, int upper) {
		this(solver, new IntegerDomain(new Interval(lower, upper)));
	}

	public IntegerVariable(Solver solver, IntegerDomain domain) {
		super(solver, domain);
	}

	//
	// Variable factories

	public IntegerVariable add(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return add(v);
	}

	public IntegerVariable add(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addVariable(v);

		getSolver().addConstraint(new IntegerSumConstraint(this, variable, v), this, variable, v);
		return v;
	}

	public IntegerVariable sub(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return sub(v);
	}

	public IntegerVariable sub(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addVariable(v);

		getSolver().addConstraint(new IntegerDifferenceConstraint(this, variable, v), this, variable, v);
		return v;
	}

	public IntegerVariable mul(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return mul(v);
	}

	public IntegerVariable mul(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addVariable(v);

		getSolver().addConstraint(new IntegerProductConstraint(this, variable, v), this, variable, v);
		return v;
	}

	public IntegerVariable div(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return div(v);
	}

	public IntegerVariable div(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addVariable(v);

		getSolver().addConstraint(new IntegerQuotientConstraint(this, variable, v), this, variable, v);
		return v;
	}

	//
	// Static constraint helpers

	public static void allSame(IntegerVariable... variables) {
		variables[0].getSolver().addConstraint(new IntegerEqualityConstraint(variables), variables);
	}

	public static void allDiff(IntegerVariable... variables) {
		variables[0].getSolver().addConstraint(new IntegerInequalityConstraint(variables), variables);
	}

	public static IntegerVariable min(IntegerVariable... variables) {
		IntegerVariable v = new IntegerVariable(variables[0].getSolver());
		variables[0].getSolver().addVariable(v);

		IntegerVariable[] vars = new IntegerVariable[variables.length + 1];

		vars[vars.length - 1] = v;
		for (int i = 0; i < variables.length; i++) {
			vars[i] = variables[i];
		}

		variables[0].getSolver().addConstraint(new IntegerMinConstraint(v, variables), vars);
		return v;
	}

	public static IntegerVariable max(IntegerVariable... variables) {
		IntegerVariable v = new IntegerVariable(variables[0].getSolver());
		variables[0].getSolver().addVariable(v);

		IntegerVariable[] vars = new IntegerVariable[variables.length + 1];

		vars[vars.length - 1] = v;
		for (int i = 0; i < variables.length; i++) {
			vars[i] = variables[i];
		}

		variables[0].getSolver().addConstraint(new IntegerMaxConstraint(v, variables), vars);
		return v;
	}

	//
	// Constraint helpers

	public IntegerVariable eq(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return eq(v);
	}

	public IntegerVariable eq(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerEqualityConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable ne(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return ne(v);
	}

	public IntegerVariable ne(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerInequalityConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable lt(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return lt(v);
	}

	public IntegerVariable lt(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerLessThanConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable le(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return le(v);
	}

	public IntegerVariable le(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerLessThanOrEqualConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable gt(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return gt(v);
	}

	public IntegerVariable gt(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerGreaterThanConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable ge(int value) {
		IntegerVariable v = new IntegerVariable(getSolver(), value);
		getSolver().addVariable(v);

		return ge(v);
	}

	public IntegerVariable ge(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerGreaterThanOrEqualConstraint(this, variable), this, variable);
		return this;
	}
}
