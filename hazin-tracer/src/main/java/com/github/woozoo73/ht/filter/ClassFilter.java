/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.woozoo73.ht.filter;

import org.aspectj.lang.JoinPoint;

import com.github.woozoo73.ht.util.AntPathMatcher;

/**
 * Class filter using Ant style path.
 * 
 * @author woozoo73
 */
public class ClassFilter implements Filter {

	private String pattern;

	private AntPathMatcher matcher = new AntPathMatcher();

	public ClassFilter() {
		this.pattern = System.getProperty("ht.classfilter.pattern", "*");
	}

	@Override
	public boolean accept(JoinPoint joinPoint) {
		return matcher.match(pattern, joinPoint.getSignature().getDeclaringType().getName());
	}

}
