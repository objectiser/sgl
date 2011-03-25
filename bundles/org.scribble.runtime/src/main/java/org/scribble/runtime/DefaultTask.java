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

import java.util.logging.Level;

import org.scribble.runtime.Context;
import org.scribble.runtime.Task;

public abstract class DefaultTask implements Task, java.io.Serializable {
	
	private boolean m_completed=false;
	protected int m_dependencies=0;
	private DefaultTask m_task=null;
	private DefaultTask m_inheritedTask=null;
	
	private static final java.util.logging.Logger _log=
		java.util.logging.Logger.getLogger(DefaultTask.class.getName());

	public DefaultTask(DefaultTask parent) {
		
		if (_log.isLoggable(Level.FINE)) {
			_log.info("CREATING TASK: "+this+" (parent="+parent+")");
		}
		
		if (parent != null) {
			m_inheritedTask = parent.m_task;
			
			if (m_inheritedTask == null) {
				m_inheritedTask = parent.m_inheritedTask;
			}
			
			if (m_inheritedTask != null) {
				scheduleOnCompletion(m_inheritedTask);
			}
		}
	}
	
	public void started(Context context) {
		if (_log.isLoggable(Level.FINE)) {
			_log.info("Started: "+this);
		}
	}
	
	public void completed(Context context) {
		m_completed = true;
		
		if (_log.isLoggable(Level.FINE)) {
			_log.info("Completed: "+this);
		}
		
		if (m_task != null && m_task.decrementDependencies() <= 0) {
			m_task.onMessage(context, null);
			
			if (m_task.isCompleted() == false) {
				context.schedule(m_task);
			}
		}
	}
	
	public boolean isCompleted() {
		return(m_completed);
	}
	
	public void scheduleOnCompletion(Task task) {
		
		if (m_task != null) {
			throw new RuntimeException("Task already has an associated 'completion' task");
		} else if ((task instanceof DefaultTask) == false) {
			throw new IllegalArgumentException("Unsupport task type");
		}
		
		m_task = (DefaultTask)task;
		
		if (_log.isLoggable(Level.FINE)) {
			_log.info(this+" scheduling task "+task);
		}
		
		m_task.incrementDependencies();
	}
	
	protected void incrementDependencies() {
		m_dependencies++;
		if (_log.isLoggable(Level.FINE)) {
			_log.info("TASK["+this+"] increment="+m_dependencies);
		}
	}
	
	protected int decrementDependencies() {
		--m_dependencies;
		if (_log.isLoggable(Level.FINE)) {
			_log.info("TASK["+this+"] decrement="+m_dependencies);
		}
		
		return(m_dependencies);
	}
}
