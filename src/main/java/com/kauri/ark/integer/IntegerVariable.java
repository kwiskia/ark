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
		return add(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable add(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addConstraint(new IntegerSumConstraint(this, variable, v), this, variable, v);
		return v;
	}

	public IntegerVariable sub(int value) {
		return sub(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable sub(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addConstraint(new IntegerDifferenceConstraint(this, variable, v), this, variable, v);
		return v;
	}

	public IntegerVariable mul(int value) {
		return mul(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable mul(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addConstraint(new IntegerProductConstraint(this, variable, v), this, variable, v);
		return v;
	}

	public IntegerVariable div(int value) {
		return div(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable div(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addConstraint(new IntegerQuotientConstraint(this, variable, v), this, variable, v);
		return v;
	}

	public IntegerVariable abs() {
		return abs(new IntegerVariable(getSolver()));
	}

	public IntegerVariable abs(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerAbsoluteValueConstraint(this, variable), this, variable);
		return variable;
	}

	public IntegerVariable sign() {
		return sign(new IntegerVariable(getSolver()));
	}

	public IntegerVariable sign(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerSignConstraint(this, variable), this, variable);
		return variable;
	}

	public IntegerVariable neg() {
		return neg(new IntegerVariable(getSolver()));
	}

	public IntegerVariable neg(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerNegationConstraint(this, variable), this, variable);
		return variable;
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
		return eq(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable eq(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerEqualityConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable ne(int value) {
		return ne(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable ne(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerInequalityConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable lt(int value) {
		return lt(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable lt(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerLessThanConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable le(int value) {
		return le(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable le(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerLessThanOrEqualConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable gt(int value) {
		return gt(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable gt(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerGreaterThanConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable ge(int value) {
		return ge(new IntegerVariable(getSolver(), value));
	}

	public IntegerVariable ge(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerGreaterThanOrEqualConstraint(this, variable), this, variable);
		return this;
	}

	public IntegerVariable between(int lower, int upper) {
		return between(new IntegerVariable(getSolver(), lower), new IntegerVariable(getSolver(), upper));
	}

	public IntegerVariable between(IntegerVariable lower, IntegerVariable upper) {
		ge(lower);
		le(upper);
		return this;
	}
}
