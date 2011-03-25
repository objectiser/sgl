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
package org.scribble.runtime.manager.inmemory;

import org.scribble.runtime.Process;
import org.scribble.runtime.manager.AbstractProcessManager;
import org.scribble.runtime.messaging.Endpoint;

/**
 * This class provides the in-memory process manager implementation.
 *
 */
public class InMemoryProcessManager extends AbstractProcessManager {

	//private static final java.util.logging.Logger _log=
	//	java.util.logging.Logger.getLogger(InMemoryProcessManager.class.getName());
	
	private java.util.Map<Endpoint, Process> m_processStore=
		new java.util.HashMap<Endpoint, Process>();
	
	/**
	 * This method retrieves the process associated with the supplied
	 * endpoint.
	 * 
	 * @param endpoint The endpoint
	 * @return The process, or null if not found
	 */
	protected Process getProcess(Endpoint endpoint) {
		return(m_processStore.get(endpoint));
	}
	
	/**
	 * This method 'returns' the process to the process
	 * manager with no changes.
	 * 
	 * @param p The process
	 */
	protected void ungetProcess(Endpoint endpoint, Process p) {
	}
	
	/**
	 * This method stores the supplied process.
	 * 
	 * @param p The process
	 */
	protected void storeProcess(Process p) {
		m_processStore.put(p.getEndpoint(), p);
	}
	
	/**
	 * This method removes the supplied process.
	 * 
	 * @param p The process to remove
	 */
	protected void removeProcess(Process p) {
		m_processStore.remove(p.getEndpoint());
	}
	
	/**
	 * This method updates the supplied process.
	 * 
	 * @param p The process to update
	 */
	protected void updateProcess(Process p) {
	}
}
