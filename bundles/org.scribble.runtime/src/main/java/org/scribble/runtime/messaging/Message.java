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
package org.scribble.runtime.messaging;

/**
 * This interface represents the wrapper for a message to be
 * handled by a process in the runtime.
 *
 */
public interface Message extends java.io.Serializable {

	/**
	 * This method returns the source endpoint of the message.
	 * 
	 * @return The endpoint
	 */
	public Endpoint getSource();
		
	/**
	 * This method returns the destination endpoint of the message.
	 * 
	 * @return The endpoint
	 */
	public Endpoint getDestination();
	
	/**
	 * This method returns the message value.
	 * 
	 * @return The value
	 */
	public Object getValue();
	
}
