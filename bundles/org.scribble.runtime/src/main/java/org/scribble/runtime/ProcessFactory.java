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

import org.scribble.runtime.messaging.Endpoint;

/**
 * This interface represents a factory associated with a particular
 * process.
 *
 */
public interface ProcessFactory {

	/**
	 * This method returns the name of the process associated
	 * with the factory.
	 * 
	 * @return The name
	 */
	public String getName();
	
	/**
	 * This method returns the properties that distinguish this
	 * implementation of the process name.
	 * 
	 * @return The optional process specific properties
	 */
	public java.util.Properties getProperties();
	
	/**
	 * This method returns the set of process names that
	 * this process implements.
	 * 
	 * @return The set of implemented process names
	 */
	public java.util.Set<String> getImplements();
	
	/**
	 * This method returns the endpoint associated with the process
	 * definition.
	 * 
	 * @return The endpoint for the process definition
	 */
	public Endpoint getEndpoint();
	
	/**
	 * This method creates a new instance of the process.
	 * 
	 * @return The newly created process instance
	 */
	public Process createProcess();
	
}
