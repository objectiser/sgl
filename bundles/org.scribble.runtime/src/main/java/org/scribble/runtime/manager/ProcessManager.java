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
package org.scribble.runtime.manager;

import org.scribble.runtime.ProcessFactory;
import org.scribble.runtime.registry.ProcessRegistry;

/**
 * This interface represents the process manager that is responsible
 * for managing the process instances and the dispatching of messages
 * to them.
 *
 */
public interface ProcessManager {
	
	/**
	 * This method returns the process registry.
	 * 
	 * @return The process registry
	 */
	public ProcessRegistry getProcessRegistry();

	/**
	 * This method registers the factory associated with a
	 * process definition.
	 * 
	 * @param factory The factory
	 */
	public void register(ProcessFactory factory);
	
	/**
	 * This method unregisters the factory associated with a
	 * process definition.
	 * 
	 * @param factory The factory
	 */
	public void unregister(ProcessFactory factory);
	
}
