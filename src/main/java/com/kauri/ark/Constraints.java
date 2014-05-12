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
			solver.addConstraint(new EqualityConstraint<>(vars[i], vars[i + 1]), vars[i], vars[i + 1]);
		}
	}

	public static <T> void makeAllUniqueConstraint(Solver solver, FiniteDomainVariable<T>... vars) {
		for (int i = 0; i < vars.length; i++) {
			for (int j = i + 1; j < vars.length; j++) {
				solver.addConstraint(new InequalityConstraint<>(vars[i], vars[j]), vars[i], vars[j]);
			}
		}
	}

	public static <T> void makeAllEqualConstraint(Solver solver, IntegerVariable... vars) {
		for (int i = 0; i < vars.length - 1; i++) {
			solver.addConstraint(new IntervalEqualityConstraint(vars[i], vars[i + 1]), vars[i], vars[i + 1]);
		}
	}

	public static <T> void makeAllUniqueConstraint(Solver solver, IntegerVariable... vars) {
		for (int i = 0; i < vars.length; i++) {
			for (int j = i + 1; j < vars.length; j++) {
				solver.addConstraint(new IntervalInequalityConstraint(vars[i], vars[j]), vars[i], vars[j]);
			}
		}
	}

	public static void makeQuotientConstraint(Solver solver, IntegerVariable a, IntegerVariable b, IntegerVariable c) {
		IntegerVariable z = new IntegerVariable(solver, new Interval(0, 0));
		solver.addVariable(z);

		solver.addConstraint(new ProductConstraint(b, c, a), a, b, c);
		solver.addConstraint(new IntervalInequalityConstraint(b, z), b, z);
	}

	public static void makeDifferenceConstraint(Solver solver, IntegerVariable a, IntegerVariable b, IntegerVariable c) {
		solver.addConstraint(new SumConstraint(b, c, a), a, b, c);
	}
}
