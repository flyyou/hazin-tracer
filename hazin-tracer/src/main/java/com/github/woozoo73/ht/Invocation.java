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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.aspectj.lang.JoinPoint;

import com.github.woozoo73.ht.jdbc.JdbcInfo;

/**
 * Invocation data.
 * 
 * @author woozoo73
 */
@XmlRootElement(name = "invocation")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "joinPointInfo", "jdbcInfo", "childInvocationList", "returnValueInfo", "throwableInfo" })
public class Invocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private Integer depth = 0;

	@XmlElementWrapper(name = "childInvocationList")
	@XmlElement(name = "invocation")
	private List<Invocation> childInvocationList;

	private JoinPoint joinPoint;

	@XmlElement(name = "joinPoint")
	private JoinPointInfo joinPointInfo;

	@XmlElement(name = "jdbc")
	private JdbcInfo jdbcInfo;

	@XmlAttribute
	private Long durationNanoTime;

	@XmlAttribute
	private Double durationPercentage = 100D;

	private Long startNanoTime;

	private Long endNanoTime;

	private Object returnValue;

	@XmlElement(name = "returnValue")
	private ObjectInfo returnValueInfo;

	private Throwable t;

	@XmlElement(name = "t")
	private ObjectInfo throwableInfo;

	protected boolean equalsJoinPoint(Invocation another) {
		if (another == null) {
			return false;
		}

		return equalsJoinPoint(another.joinPoint);
	}

	protected boolean equalsJoinPoint(JoinPoint anotherJoinPoint) {
		if (joinPoint == null) {
			return false;
		}

		return joinPoint.equals(anotherJoinPoint);
	}

	protected Invocation getInvocationByJoinPoint(JoinPoint search) {
		if (search == null) {
			return null;
		}

		if (equalsJoinPoint(search)) {
			return this;
		}

		if (childInvocationList != null) {
			for (Invocation childInvocation : childInvocationList) {
				Invocation match = childInvocation.getInvocationByJoinPoint(search);
				if (match != null) {
					return match;
				}
			}
		}

		return null;
	}

	protected void start() {
		startNanoTime = System.nanoTime();
	}

	protected void stop() {
		endNanoTime = System.nanoTime();
		durationNanoTime = endNanoTime - startNanoTime;
	}

	public void add(Invocation childInvocation) {
		childInvocation.setDepth(depth + 1);

		if (childInvocationList == null) {
			childInvocationList = new ArrayList<Invocation>();
		}
		childInvocationList.add(childInvocation);
	}

	protected void calculateChildDurationPercentage() {
		Long totalSibling = 0L;

		if (childInvocationList != null) {
			for (Invocation invocation : childInvocationList) {
				if (invocation.durationNanoTime != null) {
					totalSibling += invocation.durationNanoTime;
				}
			}
		}

		if (childInvocationList != null) {
			for (Invocation invocation : childInvocationList) {
				Double percentage = 0D;
				if (totalSibling > 0L) {
					percentage = (100D * invocation.durationNanoTime) / totalSibling;
				}
				invocation.setDurationPercentage(percentage);

				invocation.calculateChildDurationPercentage();
			}
		}
	}

	public Double getDurationMiliTime() {
		if (durationNanoTime == null) {
			return null;
		}

		return durationNanoTime.doubleValue() / (1000 * 1000);
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public List<Invocation> getChildInvocationList() {
		return childInvocationList;
	}

	public JoinPoint getJoinPoint() {
		return joinPoint;
	}

	public void setJoinPoint(JoinPoint joinPoint) {
		this.joinPoint = joinPoint;
	}

	public JoinPointInfo getJoinPointInfo() {
		return joinPointInfo;
	}

	public void setJoinPointInfo(JoinPointInfo joinPointInfo) {
		this.joinPointInfo = joinPointInfo;
	}

	public JdbcInfo getJdbcInfo() {
		return jdbcInfo;
	}

	public void setJdbcInfo(JdbcInfo jdbcInfo) {
		this.jdbcInfo = jdbcInfo;
	}

	public Long getDurationNanoTime() {
		return durationNanoTime;
	}

	public void setDurationNanoTime(Long durationNanoTime) {
		this.durationNanoTime = durationNanoTime;
	}

	public Double getDurationPercentage() {
		return durationPercentage;
	}

	public void setDurationPercentage(Double durationPercentage) {
		this.durationPercentage = durationPercentage;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public ObjectInfo getReturnValueInfo() {
		return returnValueInfo;
	}

	public void setReturnValueInfo(ObjectInfo returnValueInfo) {
		this.returnValueInfo = returnValueInfo;
	}

	public Throwable getT() {
		return t;
	}

	public void setT(Throwable t) {
		this.t = t;
	}

	public ObjectInfo getThrowableInfo() {
		return throwableInfo;
	}

	public void setThrowableInfo(ObjectInfo throwableInfo) {
		this.throwableInfo = throwableInfo;
	}

}
