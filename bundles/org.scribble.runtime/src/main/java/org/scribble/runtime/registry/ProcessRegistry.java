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
package org.scribble.runtime.registry;

import org.scribble.runtime.ProcessFactory;
import org.scribble.runtime.messaging.Endpoint;

// NOTE: How do we indicate that a process is supported locally. Could
// be part of the actor, but what happens in the process factory is then
// unregistered? How long does the actor remain valid for? What part of
// the actor is persisted with the process instance.

/**
 * This interface represents the registry, which can be used to locate
 * a reference to a process of interest.
 *
 */
public interface ProcessRegistry {

	/**
	 * This method registers the process factory, making the
	 * endpoint associated with the factory available to other
	 * processes that wish to communicate with it.
	 * 
	 * @param factory The process factory
	 */
	public void register(ProcessFactory factory);
	
	/**
	 * This method unregisters the process factory, indicating
	 * that it is no longer available at this location.
	 * 
	 * @param factory The process factory
	 */
	public void unregister(ProcessFactory factory);
	
	/**
	 * This method attempts to find an endpoint associated with the
	 * specified process name and optional properties.
	 * 
	 * @param name The process name
	 * @param props The optional properties
	 * @return The endpoint, or null if not found
	 */
	public Endpoint find(String name, java.util.Properties props);
	
}
