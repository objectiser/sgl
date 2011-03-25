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

import java.io.Serializable;

import org.scribble.runtime.messaging.DefaultMessage;
import org.scribble.runtime.messaging.Endpoint;
import org.scribble.runtime.messaging.LocalMessagingLayer;
import org.scribble.runtime.messaging.Message;
import org.scribble.runtime.messaging.MessageDispatcher;
import org.scribble.runtime.messaging.MessagingLayer;

public class DefaultClient implements Client {
	
	private MessageListener m_messageListener=null;
	private MessagingLayer m_messagingLayer=null;
	private Endpoint m_destination=null;
	private boolean m_closed=false;
	private Endpoint m_clientEndpoint=new Endpoint("Client", java.util.UUID.randomUUID());
	private static ClientMessageDispatcher m_dispatcher=null;

	public DefaultClient(Endpoint endpoint, MessagingLayer ml) {
		m_destination = endpoint;
		m_messagingLayer = ml;
		
//System.err.println("REGISTER "+m_clientEndpoint+"FOR CLIENT "+this);		
		register(ml);
	}
	
	protected synchronized void register(MessagingLayer ml) {
		if (m_dispatcher == null) {
			m_dispatcher = new ClientMessageDispatcher();
			
			ml.register(new Endpoint("Client", null), m_dispatcher);
		}
		
		m_dispatcher.register(m_clientEndpoint, this);
	}

	public void send(Serializable data) {
		Message mesg=new DefaultMessage(m_clientEndpoint, data);
		
		// If local, then optimise dispatch to endpoint
		if (m_messagingLayer instanceof LocalMessagingLayer) {
			MessageDispatcher md=((LocalMessagingLayer)m_messagingLayer).getDispatcher(m_destination);
			
			md.onMessage(m_destination, mesg);
			
		} else {
			m_messagingLayer.send(m_destination, mesg);
		}
		
		//m_brokerDispatcher = m_messagingLayer.getDispatcher(m_destination);
		
		//m_brokerDispatcher.onMessage(m_brokerEndpoint, mesg);
			
	}

	public void setMessageListener(MessageListener l) {
		m_messageListener = l;
	}

	public void onMessage(Endpoint endpoint, Message mesg) {
		
		// If destination has null id, then update from received message source
		if (m_destination.getId() == null) {
			
			if (m_destination.getName().equals(mesg.getSource().getName()) == false) {
				System.err.println("MESSAGE RECEIVED ON WRONG ENDPOINT");
			} else {
				m_destination = mesg.getSource();
			}
		}
		
		m_messageListener.onMessage(mesg);
	}
	
	public synchronized void close() {
		m_dispatcher.unregister(m_clientEndpoint);
		
		m_closed = true;
		
		notifyAll();
	}
	
	/**
	 * This method indicates whether the client has been closed.
	 * 
	 * @return Whether the client has been closed
	 */
	public boolean isClosed() {
		return(m_closed);
	}
	
	/**
	 * This method can be used to wait for the client to close.
	 * 
	 * @param waitFor The number of milliseconds to wait for closure
	 * 				of the client.
	 * @return Whether the client was successfully closed in the wait time
	 */
	public synchronized boolean waitForClose(long waitFor) {
		try {
			wait(waitFor);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return(isClosed());
	}

	public static class ClientMessageDispatcher implements MessageDispatcher {

		private java.util.Map<Endpoint, DefaultClient> m_clients=
						new java.util.HashMap<Endpoint, DefaultClient>();
		
		@Override
		public boolean onMessage(Endpoint endpoint, Message mesg) {
			DefaultClient client=m_clients.get(endpoint);
			
			if (client == null) {
				System.err.println("FAILED TO GET CLIENT FOR ENDPOINT: "+endpoint);
			} else {
				client.onMessage(endpoint, mesg);
			}
			
			return(true);
		}
		
		public void register(Endpoint endpoint, DefaultClient client) {
			m_clients.put(endpoint, client);
		}
		
		public void unregister(Endpoint endpoint) {
			m_clients.remove(endpoint);
		}
	}
}
