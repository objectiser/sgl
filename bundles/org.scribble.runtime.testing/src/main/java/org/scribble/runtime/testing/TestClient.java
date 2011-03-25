/*
 * Copyright 2010 scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.runtime.testing;

import java.io.Serializable;

import org.scribble.runtime.*;
import org.scribble.runtime.manager.inmemory.InMemoryProcessManager;
import org.scribble.runtime.messaging.LocalMessagingLayer;
import org.scribble.runtime.registry.ProcessRegistry;
import org.scribble.runtime.registry.inmemory.InMemoryProcessRegistry;

public class TestClient implements org.scribble.runtime.Client {
	
	private ScribbleRuntime m_runtime=null;
	private InMemoryProcessManager m_processManager=null;
	private org.scribble.runtime.Client m_client=null;
	
	public TestClient(ProcessFactory factory) {
		m_processManager = new InMemoryProcessManager();
		LocalMessagingLayer ml=new LocalMessagingLayer();
		m_processManager.setMessagingLayer(ml);
		
		ProcessRegistry registry=new InMemoryProcessRegistry();
		m_processManager.setProcessRegistry(registry);
		
		m_processManager.register(factory);
		
		m_runtime = new ScribbleRuntime(m_processManager, ml);
		m_client = m_runtime.createClient(factory.getName(), factory.getProperties());
	}
	
	public void addInvokedProcess(ProcessFactory factory) {
		m_processManager.register(factory);
	}

	@Override
	public void send(Serializable arg0) {
		m_client.send(arg0);
	}

	@Override
	public void setMessageListener(MessageListener arg0) {
		m_client.setMessageListener(arg0);
	}
	
	public boolean completed() {		
		return(m_client.waitForClose(10000));
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isClosed() {
		return(m_client.isClosed());
	}

	@Override
	public boolean waitForClose(long waitFor) {
		return(m_client.waitForClose(10000));
	}
}
