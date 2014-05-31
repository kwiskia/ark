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

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping
 *
 * @author Eric Fritz
 */
public class Mapping<T1, T2>
{
	private Map<T1, T2> map1 = new HashMap<>();
	private Map<T2, T1> map2 = new HashMap<>();

	public void map(T1 element1, T2 element2) {
		if (map1.containsKey(element1) || map2.containsKey(element2)) {
			throw new RuntimeException("Element already mapped.");
		}

		map1.put(element1, element2);
		map2.put(element2, element1);
	}

	public T2 getForwardMapping(T1 element) {
		if (!map1.containsKey(element)) {
			throw new RuntimeException("Element is not in map.");
		}

		return map1.get(element);
	}

	public T1 getReverseMapping(T2 element) {
		if (!map2.containsKey(element)) {
			throw new RuntimeException("Element is not in map.");
		}

		return map2.get(element);
	}
}
