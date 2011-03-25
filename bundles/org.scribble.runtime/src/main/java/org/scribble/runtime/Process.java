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
import org.scribble.runtime.messaging.Message;

/**
 * This interface represents a process instance being managed by the
 * runtime.
 *
 */
public interface Process {

	/**
	 * This method returns the endpoint for the process instance.
	 * 
	 * @return The endpoint
	 */
	public Endpoint getEndpoint();
	
	/**
	 * The name of the process.
	 * 
	 * @return The name
	 */
	public String getName();
	
	/**
	 * This method dispatches the message to the process
	 * instance.
	 * 
	 * @param context The context in which the process is running
	 * @param mesg The message
	 * @return Whether the message is handled
	 */
	public boolean dispatch(Context context, Message mesg);
	
	/**
	 * This method schedules the supplied task on the process.
	 * 
	 * @param task The task
	 * @throws IllegalArgumentException Process does not recognize the supplied task
	 */
	public void schedule(Task task) throws IllegalArgumentException;
	
	/**
	 * This method determines whether the process has completed.
	 * 
	 * @return Whether the process has completed
	 */
	public boolean isComplete();
	
}
