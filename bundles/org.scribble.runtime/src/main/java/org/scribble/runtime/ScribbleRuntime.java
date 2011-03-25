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
package org.scribble.runtime;

import org.scribble.runtime.manager.ProcessManager;
import org.scribble.runtime.messaging.Endpoint;
import org.scribble.runtime.messaging.MessagingLayer;

public class ScribbleRuntime {
	
	private ProcessManager m_processManager=null;
	private MessagingLayer m_messagingLayer=null;
	
	public ScribbleRuntime(ProcessManager pm, MessagingLayer ml) {
		m_processManager =  pm;
		m_messagingLayer = ml;
	}

	/**
	 * This method creates a client associated with the named process
	 * with optional properties.
	 * 
	 * @param name The name
	 * @param props The optional properties
	 * @return The client for communicating with the new process instance
	 */
	public Client createClient(String name, java.util.Properties props) {
		Client ret=null;
		Endpoint endpoint=m_processManager.getProcessRegistry().find(name, props);
		
		if (endpoint != null) {
			ret = new DefaultClient(new Endpoint(endpoint), m_messagingLayer);
		}
		
		return(ret);
	}
	
}
