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

import java.util.List;

import org.scribble.runtime.Context;
import org.scribble.runtime.Task;
import org.scribble.runtime.messaging.Endpoint;
import org.scribble.runtime.messaging.Message;

public class DefaultProcess implements org.scribble.runtime.Process, java.io.Serializable {

	private static final long serialVersionUID = 3558349576751492052L;

	private Endpoint m_endpoint=null;
	private String m_name=null;
	private java.util.List<Task> m_tasks=new java.util.Vector<Task>();
	private java.util.List<Task> m_toRemove=new java.util.Vector<Task>();
	
	public DefaultProcess(String name, Endpoint endpoint) {
		m_name = name;
		m_endpoint = endpoint;
	}
	
	public Endpoint getEndpoint() {
		return(m_endpoint);
	}
	
	public String getName() {
		return(m_name);
	}

	public List<Task> getTasks() {
		return(m_tasks);
	}
	
	public void schedule(Task task) throws IllegalArgumentException {
		if (isValidTask(task)) {
			m_tasks.add(task);
		} else {
			throw new IllegalArgumentException("Unknown task '"+task+"'");
		}
	}
	
	/**
	 * This method determines whether the process has completed.
	 * 
	 * @return Whether the process has completed
	 */
	public boolean isComplete() {
		return(m_tasks.size() == 0);
	}
	
	protected boolean isValidTask(Task task) {
		return(true);
	}

	public boolean dispatch(Context context, Message mesg) {
		boolean ret=false;

		for (int i=0; ret == false && i < getTasks().size(); i++) {
			Task t=getTasks().get(i);
			
			ret = t.onMessage(context, mesg);
			
			if (t.isCompleted()) {
				m_toRemove.add(t);
			}
		}
		
		// Remove completed tasks
		if (m_toRemove.size() > 0) {
			m_tasks.removeAll(m_toRemove);
			m_toRemove.clear();
		}
		
		return(ret);
	}

}
