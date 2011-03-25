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

import java.util.logging.Level;

import org.scribble.runtime.messaging.Endpoint;
import org.scribble.runtime.messaging.Message;
import org.scribble.runtime.messaging.MessageDispatcher;
import org.scribble.runtime.messaging.MessagingLayer;

public class LocalMessagingLayer implements MessagingLayer {

	private static final java.util.logging.Logger _log=
		java.util.logging.Logger.getLogger(LocalMessagingLayer.class.getName());

	private java.util.Map<String,MessageDispatcher> m_dispatchers=
				new java.util.HashMap<String, MessageDispatcher>();
	
	public MessageDispatcher getDispatcher(Endpoint endpoint) {
		return(m_dispatchers.get(endpoint.getName()));
	}
	
	@Override
	public void register(Endpoint endpoint, MessageDispatcher dispatcher) {
		//System.out.println("REGISTER ENDPOINT: "+endpoint+" (dispatcher="+dispatcher+")");
		m_dispatchers.put(endpoint.getName(), dispatcher);
	}

	@Override
	public void unregister(Endpoint endpoint) {
		m_dispatchers.remove(endpoint.getName());
	}

	@Override
	public void send(final Endpoint endpoint, final Message message) {
		
		if (_log.isLoggable(Level.FINEST)) {
			_log.info("SEND["+endpoint+"]: "+message.getValue());
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					synchronized(this) {
						wait(40);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				MessageDispatcher md=m_dispatchers.get(endpoint.getName());

				if (_log.isLoggable(Level.FINEST)) {
					_log.info("DISPATCH["+endpoint+"]: "+message.getValue());
				}
				
//System.err.println("SENDING MESG="+message+" to endpoint="+endpoint);			
				md.onMessage(endpoint, message);
			}
		}).start();
	}

}
