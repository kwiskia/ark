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

/**
 * Constraints
 *
 * @author Eric Fritz
 */
public class Constraints
{
	public static <T> void makeAllEqualConstraint(Solver solver, FiniteDomainVariable<T>... vars) {
		for (int i = 0; i < vars.length - 1; i++) {
			new EqualityConstraint<>(solver, vars[i], vars[i + 1]);
		}
	}

	public static <T> void makeAllUniqueConstraint(Solver solver, FiniteDomainVariable<T>... vars) {
		for (int i = 0; i < vars.length; i++) {
			for (int j = i + 1; j < vars.length; j++) {
				new InequalityConstraint<>(solver, vars[i], vars[j]);
			}
		}
	}

	public static <T> void makeAllEqualConstraint(Solver solver, IntegerVariable... vars) {
		for (int i = 0; i < vars.length - 1; i++) {
			new IntervalEqualityConstraint(solver, vars[i], vars[i + 1]);
		}
	}

	public static <T> void makeAllUniqueConstraint(Solver solver, IntegerVariable... vars) {
		for (int i = 0; i < vars.length; i++) {
			for (int j = i + 1; j < vars.length; j++) {
				new IntervalInequalityConstraint(solver, vars[i], vars[j]);
			}
		}
	}

	public static void makeQuotientConstraint(Solver solver, IntegerVariable a, IntegerVariable b, IntegerVariable c) {
		IntegerVariable z = new IntegerVariable("z", new Interval(0, 0));
		solver.addVariable(z);

		new ProductConstraint(solver, b, c, a);
		new IntervalInequalityConstraint(solver, b, z);
	}

	public static void makeDifferenceConstraint(Solver solver, IntegerVariable a, IntegerVariable b, IntegerVariable c) {
		new SumConstraint(solver, b, c, a);
	}
}
