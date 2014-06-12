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
 * A variable with an <tt>IntegerDomain</tt>.
 *
 * @author Eric Fritz
 */
public class IntegerVariable extends Variable<IntegerDomain>
{
	/**
	 * Creates a new IntegerVariable.
	 *
	 * @param solver The solver.
	 */
	public IntegerVariable(Solver solver) {
		this(solver, new IntegerDomain(new Interval(Interval.MIN_VALUE, Interval.MAX_VALUE)));
	}

	/**
	 * Creates a new IntegerVariable.
	 *
	 * @param solver The solver.
	 * @param value  The singleton domain value.
	 */
	public IntegerVariable(Solver solver, int value) {
		this(solver, value, value);
	}

	/**
	 * Creates a new IntegerVariable.
	 *
	 * @param solver The solver.
	 * @param lower  The lower bound of the domain.
	 * @param upper  The upper bound of the domain.
	 */
	public IntegerVariable(Solver solver, int lower, int upper) {
		this(solver, new IntegerDomain(new Interval(lower, upper)));
	}

	/**
	 * Creates a new IntegerVariable.
	 *
	 * @param solver The solver.
	 * @param domain The domain.
	 */
	public IntegerVariable(Solver solver, IntegerDomain domain) {
		super(solver, domain);
	}

	/**
	 * Creates a series of constraints so that each supplied variable have equivalent values.
	 *
	 * @param variables The set of variables to constrain.
	 */
	public static void allSame(IntegerVariable... variables) {
		for (int i = 0; i < variables.length - 1; i++) {
			IntegerVariable var1 = variables[i];
			IntegerVariable var2 = variables[i + 1];

			variables[i].getSolver().addConstraint(new IntegerEqualityConstraint(var1, var2), var1, var2);
		}
	}

	/**
	 * Creates a series of constraints so that each supplied variable have distinct values.
	 *
	 * @param variables The set of variables to constrain.
	 */
	public static void allDiff(IntegerVariable... variables) {
		for (int i = 0; i < variables.length - 1; i++) {
			for (int j = i + 1; j < variables.length; j++) {
				IntegerVariable var1 = variables[i];
				IntegerVariable var2 = variables[j];

				variables[i].getSolver().addConstraint(new IntegerInequalityConstraint(var1, var2), var1, var2);
			}
		}
	}

	/**
	 * Creates a new variable which is the minimum value of the supplied variables.
	 *
	 * @param variables The set of variables to constrain.
	 *
	 * @return The minimum variable.
	 */
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

	/**
	 * Creates a new variable which is the maximum value of the supplied variables.
	 *
	 * @param variables The set of variables to constrain.
	 *
	 * @return The maximum variable.
	 */
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

	/**
	 * Creates a new variable which is the sum of this integer variable and <tt>value</tt>.
	 *
	 * @param value The addend.
	 *
	 * @return The sum variable.
	 */
	public IntegerVariable add(int value) {
		return add(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a new variable which is the sum of this integer variable and <tt>variable</tt>.
	 *
	 * @param variable The addend.
	 *
	 * @return The sum variable.
	 */
	public IntegerVariable add(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addConstraint(new IntegerSumConstraint(this, variable, v), this, variable, v);
		return v;
	}

	/**
	 * Creates a new variable which is the difference of this integer variable and <tt>value</tt>.
	 *
	 * @param value The subtrahend.
	 *
	 * @return The difference variable.
	 */
	public IntegerVariable sub(int value) {
		return sub(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a new variable which is the difference of this integer variable and <tt>variable</tt>.
	 *
	 * @param variable The subtrahend.
	 *
	 * @return The difference variable.
	 */
	public IntegerVariable sub(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addConstraint(new IntegerSumConstraint(v, variable, this), v, variable, this);
		return v;
	}

	/**
	 * Creates a new variable which is the product of this integer variable and <tt>value</tt>.
	 *
	 * @param value The multiplier.
	 *
	 * @return The product variable.
	 */
	public IntegerVariable mul(int value) {
		return mul(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a new variable which is the product of this integer variable and <tt>variable</tt>.
	 *
	 * @param variable The multiplier.
	 *
	 * @return The product variable.
	 */
	public IntegerVariable mul(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		getSolver().addConstraint(new IntegerProductConstraint(this, variable, v), this, variable, v);
		return v;
	}

	/**
	 * Creates a new variable which is the quotient of this integer variable and <tt>value</tt>.
	 *
	 * @param value The divisor.
	 *
	 * @return The quotient variable.
	 */
	public IntegerVariable div(int value) {
		return div(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a new variable which is the quotient of this integer variable and <tt>variable</tt>.
	 *
	 * @param variable The divisor.
	 *
	 * @return The quotient variable.
	 */
	public IntegerVariable div(IntegerVariable variable) {
		IntegerVariable v = new IntegerVariable(getSolver());
		variable.ne(0);
		getSolver().addConstraint(new IntegerProductConstraint(v, variable, this), v, variable, this);
		return v;
	}

	/**
	 * Creates a new variable which is the absolute value of this integer variable.
	 *
	 * @return The absolute value variable.
	 */
	public IntegerVariable abs() {
		return abs(new IntegerVariable(getSolver()));
	}

	/**
	 * Creates a constraint forcing <tt>variable</tt> to be the absolute value of this integer variable.
	 *
	 * @param variable The absolute value variable.
	 *
	 * @return <tt>variable</tt>
	 */
	public IntegerVariable abs(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerAbsoluteValueConstraint(this, variable), this, variable);
		return variable;
	}

	/**
	 * Creates a new variable which is the sign of this integer variable.
	 *
	 * @return The sign variable.
	 */
	public IntegerVariable sign() {
		return sign(new IntegerVariable(getSolver()));
	}

	/**
	 * Creates a constraint forcing <tt>variable</tt> to be the sign of this integer variable.
	 *
	 * @param variable The sign variable.
	 *
	 * @return <tt>variable</tt>
	 */
	public IntegerVariable sign(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerSignConstraint(this, variable), this, variable);
		return variable;
	}

	/**
	 * Creates a new variable which is the negation of this integer variable.
	 *
	 * @return The negated variable.
	 */
	public IntegerVariable neg() {
		return neg(new IntegerVariable(getSolver()));
	}

	/**
	 * Creates a constraint forcing <tt>variable</tt> to be the negation of this integer variable.
	 *
	 * @param variable The negated variable.
	 *
	 * @return <tt>variable</tt>
	 */
	public IntegerVariable neg(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerNegationConstraint(this, variable), this, variable);
		return variable;
	}

	/**
	 * Creates a constraint forcing this variable to be equivalent to <tt>value</tt>.
	 *
	 * @param value The value to be equal to.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable eq(int value) {
		return eq(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a constraint forcing this variable to be equivalent to <tt>variable</tt>.
	 *
	 * @param variable The variable to be equal to.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable eq(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerEqualityConstraint(this, variable), this, variable);
		return this;
	}

	/**
	 * Creates a constraint forcing this variable to be distinct from <tt>value</tt>.
	 *
	 * @param value The value to be distinct from.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable ne(int value) {
		return ne(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a constraint forcing this variable to be distinct from <tt>variable</tt>.
	 *
	 * @param variable The variable to be distinct from.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable ne(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerInequalityConstraint(this, variable), this, variable);
		return this;
	}

	/**
	 * Creates a constraint forcing this variable to be less than <tt>value</tt>.
	 *
	 * @param value The value to be less than.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable lt(int value) {
		return lt(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a constraint forcing this variable to be less than <tt>variable</tt>.
	 *
	 * @param variable The variable to be less than.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable lt(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerInequalityConstraint(this, variable), this, variable);
		getSolver().addConstraint(new IntegerLessThanOrEqualConstraint(this, variable), this, variable);
		return this;
	}

	/**
	 * Creates a constraint forcing this variable to be less than or equal to<tt>value</tt>.
	 *
	 * @param value The value to be less than or equal to.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable le(int value) {
		return le(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a constraint forcing this variable to be less than or equal to<tt>variable</tt>.
	 *
	 * @param variable The variable to be less than or equal to.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable le(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerLessThanOrEqualConstraint(this, variable), this, variable);
		return this;
	}

	/**
	 * Creates a constraint forcing this variable to be greater than <tt>value</tt>.
	 *
	 * @param value The value to be greater than.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable gt(int value) {
		return gt(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a constraint forcing this variable to be greater than <tt>variable</tt>.
	 *
	 * @param variable The variable to be greater than.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable gt(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerInequalityConstraint(variable, this), variable, this);
		getSolver().addConstraint(new IntegerLessThanOrEqualConstraint(variable, this), variable, this);
		return this;
	}

	/**
	 * Creates a constraint forcing this variable to be greater than or equal to<tt>value</tt>.
	 *
	 * @param value The value to be greater than or equal to.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable ge(int value) {
		return ge(new IntegerVariable(getSolver(), value));
	}

	/**
	 * Creates a constraint forcing this variable to be greater than or equal to<tt>variable</tt>.
	 *
	 * @param variable The variable to be greater than or equal to.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable ge(IntegerVariable variable) {
		getSolver().addConstraint(new IntegerLessThanOrEqualConstraint(variable, this), variable, this);
		return this;
	}

	/**
	 * Creates a constraint forcing this variable to be between the values <tt>lower</tt> and <tt>upper</tt>.
	 *
	 * @param lower The lower bound.
	 * @param upper The upper bound.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable between(int lower, int upper) {
		return between(new IntegerVariable(getSolver(), lower), new IntegerVariable(getSolver(), upper));
	}

	/**
	 * Creates a constraint forcing this variable to be between the variables <tt>lower</tt> and <tt>upper</tt>.
	 *
	 * @param lower The lower bound.
	 * @param upper The upper bound.
	 *
	 * @return <tt>this</tt>
	 */
	public IntegerVariable between(IntegerVariable lower, IntegerVariable upper) {
		ge(lower);
		le(upper);
		return this;
	}
}
