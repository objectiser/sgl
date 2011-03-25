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

import org.scribble.runtime.messaging.Message;

public class MutuallyExclusiveTasks implements Task, java.io.Serializable {

	private java.util.List<Task> m_tasks=new java.util.Vector<Task>();
	private Task m_selected=null;
	
	public boolean onMessage(Context context, Message mesg) {
		boolean ret=false;
		
		for (int i=0; ret == false && i < m_tasks.size(); i++) {
			Task t=m_tasks.get(i);
			
			if (ret = t.onMessage(context, mesg)) {
				m_selected = t;
			}
		}
		
		return(ret);
	}
	
	public boolean isCompleted() {
		boolean ret=false;
		
		if (m_selected != null) {
			ret = m_selected.isCompleted();
		}
		
		return(ret);
	}
	
	public void addTask(Task t) {
		m_tasks.add(t);
	}
	
	public void scheduleOnCompletion(Task t) {
		for (Task subTask : m_tasks) {
			subTask.scheduleOnCompletion(t);
		}
	}
}
