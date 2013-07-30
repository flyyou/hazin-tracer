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
package com.github.woozoo73.ht;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.github.woozoo73.ht.format.DefaultFormat;
import com.github.woozoo73.ht.format.Format;

/**
 * Invocation aspect.
 * 
 * @author woozoo73
 */
@Aspect
public class InvocationAspect {

	private static final Log logger = LogFactory.getLog(InvocationAspect.class);

	private Format format;

	public InvocationAspect() {
		initFormat();
	}

	private void initFormat() {
		try {
			String invocationformat = System.getProperty("htformat", DefaultFormat.class.getName());
			this.format = (Format) Class.forName(invocationformat).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Pointcut("!within(com.github.woozoo73.ht..*) && execution(* *.*(..))")
	public void executionPointcut() {
	}

	@Pointcut("!within(com.github.woozoo73.ht..*) && initialization(*.new(..))")
	public void initializationPointcut() {
	}

	@Before("initializationPointcut()")
	public void before(JoinPoint joinPoint) throws Throwable {
		beforeProfile(joinPoint);
	}

	@After("initializationPointcut()")
	public void after(JoinPoint joinPoint) throws Throwable {
		afterProfile(joinPoint);
	}

	@Around("executionPointcut()")
	public Object profile(ProceedingJoinPoint joinPoint) throws Throwable {
		Invocation invocation = beforeProfile(joinPoint);
		Object returnValue = null;

		try {
			returnValue = joinPoint.proceed();
			invocation.setReturnValueInfo(new ObjectInfo(returnValue));

			return returnValue;
		} catch (Throwable t) {
			invocation.setThrowableInfo(new ObjectInfo(t));

			throw t;
		} finally {
			afterProfile(joinPoint);
		}
	}

	private Invocation beforeProfile(JoinPoint joinPoint) throws Throwable {
		Invocation invocation = new Invocation();
		invocation.setJoinPoint(joinPoint);
		invocation.setJoinPointInfo(new JoinPointInfo(joinPoint));

		Invocation endpointInvocation = Context.getEndpointInvocation();
		if (endpointInvocation == null) {
			Context.setEndpointInvocation(invocation);
		}

		Invocation currentInvocation = Context.peekFromInvocationStack();
		if (currentInvocation != null) {
			currentInvocation.add(invocation);
		}

		Context.addToInvocationStack(invocation);

		invocation.start();

		return invocation;
	}

	private void afterProfile(JoinPoint joinPoint) throws Throwable {
		Invocation endpointInvocation = Context.getEndpointInvocation();
		Invocation invocation = endpointInvocation.getInvocationByJoinPoint(joinPoint);

		invocation.stop();

		Context.popFromInvocationStack();

		if (invocation.equalsJoinPoint(Context.getEndpointInvocation())) {
			Invocation i = Context.dump();

			if (logger.isDebugEnabled()) {
				logger.debug(format.format(i));
			}
		}
	}

}