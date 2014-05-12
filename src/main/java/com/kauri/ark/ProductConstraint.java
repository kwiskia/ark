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
 * ProductConstraint
 *
 * @author Eric Fritz
 */
public class ProductConstraint extends Constraint<IntegerVariable>
{
	public ProductConstraint(Solver solver, IntegerVariable a, IntegerVariable b, IntegerVariable c) {
		super(solver, a, b, c);
	}

	@Override
	public boolean update(  IntegerVariable variable) {
		IntegerVariable a = variables.get(0);
		IntegerVariable b = variables.get(1);
		IntegerVariable c = variables.get(2);

		Interval result;

		if (variable == a) {
			// c / b
			int av = c.allowableValues.getLowerBound();
			int bv = c.allowableValues.getUpperBound();
			int cv = b.allowableValues.getLowerBound();
			int dv = b.allowableValues.getUpperBound();

			if (cv == 0 || dv == 0) {
				if (a.isUnique() && c.isUnique()) {
					return c.allowableValues.getLowerBound() == 0;
				}

				return true;
			}

			result = new Interval(min(av / cv, av / dv, bv / cv, bv / dv), max(av / cv, av / dv, bv / cv, bv / dv));
		} else if (variable == b) {
			// c / a
			int av = c.allowableValues.getLowerBound();
			int bv = c.allowableValues.getUpperBound();
			int cv = a.allowableValues.getLowerBound();
			int dv = a.allowableValues.getUpperBound();

			if (cv == 0 || dv == 0) {
				if (b.isUnique() && c.isUnique()) {
					return c.allowableValues.getLowerBound() == 0;
				}

				return true;
			}

			result = new Interval(min(av / cv, av / dv, bv / cv, bv / dv), max(av / cv, av / dv, bv / cv, bv / dv));
		} else if (variable == c) {
			// a * b
			int av = a.allowableValues.getLowerBound();
			int bv = a.allowableValues.getUpperBound();
			int cv = b.allowableValues.getLowerBound();
			int dv = b.allowableValues.getUpperBound();

			result = new Interval(min(av * cv, av * dv, bv * cv, bv * dv), max(av * cv, av * dv, bv * cv, bv * dv));
		} else {
			throw new RuntimeException("Unreachable.");
		}

		result = new Interval(Math.max(variable.allowableValues.getLowerBound(), result.getLowerBound()), Math.min(variable.allowableValues.getUpperBound(), result.getUpperBound()));

		return variable.trySetValue(getSolver(), result);
	}

	private int min(int... nums) {
		int min = nums[0];

		for (int n : nums) {
			min = Math.min(min, n);
		}

		return min;
	}

	private int max(int... nums) {
		int max = nums[0];

		for (int n : nums) {
			max = Math.max(max, n);
		}

		return max;
	}
}
