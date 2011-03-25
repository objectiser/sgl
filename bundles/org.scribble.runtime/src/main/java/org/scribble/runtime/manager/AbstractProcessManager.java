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
package org.scribble.runtime.manager;

import java.io.Serializable;
import java.util.logging.Level;

import org.scribble.runtime.Context;
import org.scribble.runtime.ProcessFactory;
import org.scribble.runtime.Process;
import org.scribble.runtime.Task;
import org.scribble.runtime.messaging.DefaultMessage;
import org.scribble.runtime.messaging.Endpoint;
import org.scribble.runtime.messaging.Message;
import org.scribble.runtime.messaging.MessageDispatcher;
import org.scribble.runtime.messaging.MessagingLayer;
import org.scribble.runtime.registry.ProcessRegistry;

// TODO: Endpoint could be the URL and process instance. If process instance id is null, then
// means new instance.
// Don't have Message wrapper? Just on the wire format that conveys message content, and
// source endpoint (url+id) and destination endpoint (url+id).

// NOTE: Use m_process and m_process stack, when process being set, if already has value, push old
// value on stack. Similar after finish, pop to reset process, until empty stack then set to null.
// If new process already exists on stack, then send message via messaging layer.


/**
 * This class provides the default process manager implementation.
 *
 */
public abstract class AbstractProcessManager implements ProcessManager {

	private static final java.util.logging.Logger _log=
		java.util.logging.Logger.getLogger(AbstractProcessManager.class.getName());
	
	private java.util.Map<String, ProcessFactory> m_factoriesByName=
		new java.util.HashMap<String, ProcessFactory>();
	private java.util.Map<String, EndpointManager> m_endpointManagers=
		new java.util.HashMap<String, EndpointManager>();
	//private java.util.Map<Endpoint, Process> m_processStore=
	//		new java.util.HashMap<Endpoint, Process>();
	private MessagingLayer m_messagingLayer=null;
	private ProcessRegistry m_processRegistry=null;
	
	/**
	 * This method retrieves the process associated with the supplied
	 * endpoint.
	 * 
	 * @param endpoint The endpoint
	 * @return The process, or null if not found
	 */
	protected abstract Process getProcess(Endpoint endpoint);

	/**
	 * This method 'returns' the process to the process
	 * manager with no changes.
	 * 
	 * @param endpoint The endpoint
	 * @param p The process
	 */
	protected abstract void ungetProcess(Endpoint endpoint, Process p);

	/**
	 * This method stores the supplied process.
	 * 
	 * @param p The process
	 */
	protected abstract void storeProcess(Process p);

	/**
	 * This method updates the supplied process.
	 * 
	 * @param p The process to update
	 */
	protected abstract void updateProcess(Process p);

	/**
	 * This method removes the supplied process.
	 * 
	 * @param p The process to remove
	 */
	protected abstract void removeProcess(Process p);

	/**
	 * This method sets the messaging layer.
	 * 
	 * @param ml The messaging layer
	 */
	public void setMessagingLayer(MessagingLayer ml) {
		m_messagingLayer = ml;
	
		_log.info("Set messaging on process manager="+ml);		
		
		for (ProcessFactory factory : m_factoriesByName.values()) {
			m_messagingLayer.register(factory.getEndpoint(),
						new EndpointManager(factory, m_messagingLayer));
		}
	}
	
	public void setProcessRegistry(ProcessRegistry registry) {
		m_processRegistry = registry;
		
		_log.info("Set registry on process manager="+registry);	
		
		for (ProcessFactory factory : m_factoriesByName.values()) {
			m_processRegistry.register(factory);
		}
	}
	
	/**
	 * This method returns the process registry.
	 * 
	 * @return The process registry
	 */
	public ProcessRegistry getProcessRegistry() {
		return(m_processRegistry);
	}
	
	/**
	 * This method registers the factory associated with a
	 * process definition.
	 * 
	 * @param factory The factory
	 */
	public void register(ProcessFactory factory) {
		m_factoriesByName.put(factory.getName(), factory);
		
		// Register endpoint with messaging layer
		if (m_messagingLayer != null) {
			EndpointManager em=new EndpointManager(factory, m_messagingLayer);
			
			m_endpointManagers.put(factory.getName(), em);
			
			m_messagingLayer.register(factory.getEndpoint(), em);
		} else {
			_log.info("Register process factory '"+factory+"' but no messaging currently");
		}
		
		// Register endpoint with registry
		if (m_processRegistry != null) {
			m_processRegistry.register(factory);
		}
	}
	
	/**
	 * This method unregisters the factory associated with a
	 * process definition.
	 * 
	 * @param factory The factory
	 */
	public void unregister(ProcessFactory factory) {
		m_factoriesByName.remove(factory.getName());
				
		// Unregister endpoint from messaging layer
		if (m_messagingLayer != null) {
			m_messagingLayer.unregister(factory.getEndpoint());
		}
		
		if (m_processRegistry != null) {
			m_processRegistry.unregister(factory);
		}
	}
	
	protected EndpointManager getLocalEndpointManager(String processName) {
		EndpointManager ret=m_endpointManagers.get(processName);
		
		return(ret);
	}
	
	/**
	 * This class is responsible for managing the activity on a particular
	 * endpoint associated with a process factory.
	 *
	 */
	// TODO: think whether should be static? If not, then messaging layer could
	// also be accessed from outer class
	public class EndpointManager implements MessageDispatcher, Context {
		
		private ProcessFactory m_factory=null;
		private MessagingLayer m_messagingLayer=null;
		private Process m_process=null;
		private ThreadLocal<java.util.HashSet<Process>> m_current=new ThreadLocal<java.util.HashSet<Process>>();
		//private ThreadLocal<Process> m_process=new ThreadLocal<Process>();
		//private ThreadLocal<java.util.List<Process>> m_processStack=
		//					new ThreadLocal<java.util.List<Process>>();
		
		public EndpointManager(ProcessFactory factory, MessagingLayer messagingLayer) {
			m_factory = factory;
			m_messagingLayer = messagingLayer;
		}
		
		
		/**
		 * This method is invoked to handle a received message.
		 * 
		 * @param endpoint The endpoint that received the message
		 * @param mesg The message to be dispatched
		 * @return Whether message has been handled
		 */
		public synchronized boolean onMessage(Endpoint endpoint, Message mesg) {
			boolean ret=false;
			Process p=null;
//System.err.println("GPB: ("+Thread.currentThread()+") onMessage endpoint="+endpoint+" mesg="+mesg);			

			// Check if process instance is to be retrieved
			if (endpoint.getId() != null) {

				// Lookup process instance from the process store
				p = getProcess(endpoint);
					
				if (p == null) {
					// TODO: Report error
					System.err.println("FAILED TO FIND PROCESS WITH ID: "+endpoint);
					
					//Exception e=new Exception();
					//e.printStackTrace();
					
					ungetProcess(endpoint, p);
				}

			} else {
				// Create process instance
				p = m_factory.createProcess();
				
				// Override the channel id provided by the caller
				p.getEndpoint().setChannelId(endpoint.getChannelId());

				// TODO: Add to process store - even if transient, we need store
				// to have reference to id, in case other thread accesses it. So
				// if transaction not committed, hopefully process store won't actually
				// persist.
				storeProcess(p);
				
				if (_log.isLoggable(Level.FINEST)) {
					_log.finest("Storing new process "+p+" for endpoint "+p.getEndpoint());		
				}
			}
			
			if (p != null) {
				java.util.HashSet<Process> current=m_current.get();
				
				if (current != null && current.contains(p)) {
					if (_log.isLoggable(Level.FINEST)) {
						_log.info(">> DIVERT TO ML");		
					}
					m_messagingLayer.send(p.getEndpoint(), mesg);
					
					ungetProcess(endpoint, p);
					
				} else {
					boolean f_completed=false;
					
					synchronized(p) {
						Process oldProcess=m_process;
						
						m_process = p;
						
						if (current == null) {
							current = new java.util.HashSet<Process>();
							m_current.set(current);
						}
						
						current.add(p);
						
						if (p.dispatch(this, mesg) == false) {
							System.err.println("MISMATCHED MESSAGE: "+mesg+" PROCESS="+p+" ENDPOINT="+endpoint);
						} else {
							//System.out.println("MATCHED MESSAGE: "+mesg);
						}
						
						if (p.isComplete()) {
							
							// TODO: Remove from process store
							// ISSUE: Had case where two 'process completed' messages
							// displayed for same id - one probably from another
							// thread that actually completed the process, and
							// the other from the original thread that kicked off
							// the thread that completed the process. Should the
							// processing of the process be synchronous?
							// Sync on the process instead of the dispatch?
							
							if (_log.isLoggable(Level.FINEST)) {
								_log.info("PROCESS COMPLETED: "+p);
							}
							
							f_completed = true;
						}
						
						// Restore previous process
						m_process = oldProcess;
						
						current.remove(p);
					}
					
					if (f_completed) {
						// TODO: Remove process from process store?? Previously
						// had 'update process in process store'??
						
						// Remove the process
						removeProcess(p);
					} else {
						// Update the process
						updateProcess(p);
					}
				}
				
				ret = true;
			}
			
			return(ret);
		}
		
		/**
		 * This method sends the message to the specified actor.
		 * 
		 * @param actor The actor
		 * @param mesg The message
		 */
		public void send(final Endpoint endpoint, Serializable mesg) {
//System.err.println("GPB: ("+Thread.currentThread()+") send endpoint="+endpoint+" mesg="+mesg);			
			
			DefaultMessage m=new DefaultMessage(m_process.getEndpoint(), mesg);
			
			// Get endpoint for actor
		
			// Check if local
			final EndpointManager em=getLocalEndpointManager(endpoint.getName());
			final DefaultMessage dm=m;
			
			if (em != null) {
				boolean handled=false;
				//int retry=0;
				
				//do {
					handled = em.onMessage(endpoint, dm);
					//retry++;
				//} while (handled == false && retry < 100);
				
				if (handled == false) {
					System.err.println("FAILED TO HANDLE MESSAGE: "+endpoint+" mesg="+mesg);
				//} else if (retry > 1) {
				//	System.err.println("HANDLED AFTER "+retry+" RETRIES: "+endpoint+" mesg="+mesg);
				}
			} else {
				// TODO: How to associate actor (or source endpoint info) with message?
				m_messagingLayer.send(endpoint, m);
			}
		}
		
		/**
		 * This method creates a proxy representing an actor. If
		 * more than one type of the actor may exist, then properties
		 * can be used to distinguish the actor of interest.
		 * 
		 * @param name The name of the actor
		 * @param props The properties
		 * @return The actor
		 */
		public Endpoint find(String name, java.util.Properties props) {
			Endpoint ret=null;
			
			/*
			if (name.equals("example.Supplier")) {
				ret = new Endpoint("RequestForQuote-Supplier1",null);
			}
			*/
			if (m_processRegistry != null) {
				ret = m_processRegistry.find(name, props);
			} else {
				_log.severe("No process registry found");
			}
			
			if (ret == null) {
				_log.severe("Failed to find endpoint for '"+name+"' with properties: "+props);
			} else {
				ret = new Endpoint(ret);
			}
			
			return(ret);
		}

		/**
		 * This method can be used to schedule a task with the
		 * process instance.
		 * 
		 * @param task The task
		 */
		public void schedule(Task task) {
			m_process.schedule(task);
		}
	}
}
