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
 * This interface represents the message dispatcher responsible
 * for handling incoming messages.
 *
 */
public interface MessageDispatcher {

	/**
	 * This method is invoked to handle a received message.
	 * 
	 * @param endpoint The endpoint that received the message
	 * @param mesg The message to be dispatched
	 * @return Whether the message is handler
	 */
	public boolean onMessage(Endpoint endpoint, Message mesg);
	
}
