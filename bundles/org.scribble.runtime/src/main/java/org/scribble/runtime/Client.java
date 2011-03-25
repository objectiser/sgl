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

/**
 * This interface represents a client of the Scribble runtime.
 * 
 */
public interface Client {

	/**
	 * This method sends the message to the service associated
	 * with the client.
	 * 
	 * @param mesg The message
	 */
	public void send(java.io.Serializable mesg);
	
	/**
	 * This method sets the message listener for receiving messages
	 * returned to the client.
	 * 
	 * @param l The listener
	 */
	public void setMessageListener(MessageListener l);
	
	/**
	 * This method closes the client.
	 * 
	 */
	public void close();
	
	/**
	 * This method indicates whether the client has been closed.
	 * 
	 * @return Whether the client has been closed
	 */
	public boolean isClosed();

	/**
	 * This method can be used to wait for the client to close.
	 * 
	 * @param waitFor The number of milliseconds to wait for closure
	 * 				of the client.
	 * @return Whether the client was successfully closed in the wait time
	 */
	public boolean waitForClose(long waitFor);
	
}
