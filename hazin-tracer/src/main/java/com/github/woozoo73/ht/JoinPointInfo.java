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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

/**
 * JoinPoint data.
 * 
 * @author woozoo73
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "target", "signatureInfo", "argsInfo", "sourceLocation" })
public class JoinPointInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private ObjectInfo target;

	@XmlElement(name = "signature")
	private SignatureInfo signatureInfo;

	private Object[] args;

	@XmlElementWrapper(name = "args")
	@XmlElement(name = "arg")
	private ObjectInfo[] argsInfo;

	@XmlElement(name = "sourceLocation")
	private SourceLocationInfo sourceLocation;

	public JoinPointInfo() {
	}

	public JoinPointInfo(JoinPoint joinPoint) {
		if (joinPoint == null) {
			return;
		}

		target = new ObjectInfo(joinPoint.getTarget());

		args = joinPoint.getArgs();
		if (args != null && args.length > 0) {
			argsInfo = new ObjectInfo[args.length];
			for (int i = 0; i < argsInfo.length; i++) {
				argsInfo[i] = new ObjectInfo(args[i]);
			}
		}

		sourceLocation = new SourceLocationInfo(joinPoint.getSourceLocation());

		Signature signature = joinPoint.getSignature();
		if (signature == null) {
			return;
		}

		signatureInfo = new SignatureInfo(signature);
	}

	public ObjectInfo getTarget() {
		return target;
	}

	public void setTarget(ObjectInfo target) {
		this.target = target;
	}

	public SignatureInfo getSignatureInfo() {
		return signatureInfo;
	}

	public void setSignatureInfo(SignatureInfo signatureInfo) {
		this.signatureInfo = signatureInfo;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public ObjectInfo[] getArgsInfo() {
		return argsInfo;
	}

	public void setArgsInfo(ObjectInfo[] argsInfo) {
		this.argsInfo = argsInfo;
	}

	public SourceLocationInfo getSourceLocation() {
		return sourceLocation;
	}

	public void setSourceLocation(SourceLocationInfo sourceLocation) {
		this.sourceLocation = sourceLocation;
	}

}
