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
 * This interface represents the context used by the process instance
 * when executing.
 *
 */
public interface Context {

	/**
	 * This method sends the message to the specified endpoint.
	 * 
	 * @param endpoint The endpoint
	 * @param mesg The message
	 */
	public void send(Endpoint endpoint, java.io.Serializable mesg);
	
	/**
	 * This method retrieves an endpoint associated with a process
	 * and an optional set of properties. If
	 * more than one type of the process may exist, then properties
	 * can be used to distinguish the process implementation of interest.
	 * 
	 * @param name The name of the endpoint process
	 * @param props The properties
	 * @return The endpoint
	 */
	public Endpoint find(String name, java.util.Properties props);

	/**
	 * This method can be used to schedule a task with the
	 * process instance.
	 * 
	 * @param task The task
	 */
	public void schedule(Task task);

}
