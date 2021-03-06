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
package com.github.woozoo73.test.dummy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.woozoo73.ht.callback.StatisticsRecorder;
import com.github.woozoo73.test.AbstractSpringTestCase;

public class ProcessorTest extends AbstractSpringTestCase {

	protected static final Log logger = LogFactory.getLog(ProcessorTest.class);

	@Autowired
	private Processor processor;

	private Server server;

	private String databasePath = "/hsqldb/test";

	@Before
	public void setUp() throws Exception {
		startHsqldb();
	}

	@After
	public void tearDown() throws Exception {
		shutdownHsqldb();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		showStatistics();
	}

	protected void startHsqldb() {
		server = new Server();
		server.setLogWriter(null);
		// server.setLogWriter(new PrintWriter(System.out));
		// server.setErrWriter(new PrintWriter(System.out));
		server.setDatabasePath(0, databasePath);
		server.setDatabaseName(0, "test");
		server.start();
	}

	protected void shutdownHsqldb() {
		try {
			server.shutdown();
		} catch (Exception t) {
			logger.warn(t.getMessage(), t);
		}
	}

	protected static void showStatistics() {
		logger.debug(StatisticsRecorder.prettyPrint());
	}

	@Test
	public void testProcess() {
		processor.process("foo");
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessWithNull() {
		processor.process(null);
	}

}
