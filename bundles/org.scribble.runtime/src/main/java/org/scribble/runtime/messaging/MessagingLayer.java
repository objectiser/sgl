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
 * This interface represents the messaging layer used to communicate
 * between processes.
 *
 */
public interface MessagingLayer {
	
	/**
	 * This method sends the message to the endpoint.
	 * 
	 * @param endpoint The endpoint
	 * @param message The message
	 */
	public void send(Endpoint endpoint, Message message);
	
	/**
	 * This method informs the messaging layer to subscribe
	 * for messages on the supplied endpoint, and report them to
	 * the supplier dispatcher.
	 * 
	 * @param endpoint The endpoint
	 * @param dispatcher The message dispatcher
	 */
	public void register(Endpoint endpoint, MessageDispatcher dispatcher);
	
	/**
	 * This method informs the messaging layer to no longer
	 * receive messages on the supplied endpoint.
	 * 
	 * @param endpoint The endpoint
	 */
	public void unregister(Endpoint endpoint);
	
}
