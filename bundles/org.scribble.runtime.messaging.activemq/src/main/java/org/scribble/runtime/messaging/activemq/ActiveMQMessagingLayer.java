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
package org.scribble.runtime.messaging.activemq;

import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.scribble.runtime.messaging.DefaultMessage;
import org.scribble.runtime.messaging.Endpoint;
import org.scribble.runtime.messaging.LocalMessagingLayer;
import org.scribble.runtime.messaging.Message;
import org.scribble.runtime.messaging.MessageDispatcher;

public class ActiveMQMessagingLayer implements org.scribble.runtime.messaging.MessagingLayer {

	private static final java.util.logging.Logger _log=
		java.util.logging.Logger.getLogger(LocalMessagingLayer.class.getName());

	private java.util.Map<String,javax.jms.MessageConsumer> m_consumers=
				new java.util.HashMap<String, javax.jms.MessageConsumer>();
	
    private String user = ActiveMQConnection.DEFAULT_USER;
    private String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private int ackMode = Session.CLIENT_ACKNOWLEDGE;
    private boolean transacted=false;
    private Session session;

    private boolean f_initialized=false;

    public ActiveMQMessagingLayer() {
	}
	
	protected synchronized void init() {
		try {
	        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
	        Connection connection = connectionFactory.createConnection();
	        
            connection.start();

            session = connection.createSession(transacted, ackMode);

            f_initialized = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void register(final Endpoint endpoint, final MessageDispatcher dispatcher) {
		if (f_initialized == false) {
			init();
		}
		
		try {
			javax.jms.Queue queue=session.createQueue(getQueueName(endpoint));
			
			javax.jms.MessageConsumer receiver=session.createConsumer(queue);
			
			receiver.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(javax.jms.Message arg0) {
					if (arg0 instanceof javax.jms.ObjectMessage) {
						try {
							Message mesg=(Message)((javax.jms.ObjectMessage)arg0).getObject();
							
							Endpoint ep=mesg.getDestination();
//System.err.println("GPB: JMS RECEIVE: "+new java.util.Date()+" ("+Thread.currentThread()+"): endpoint="+endpoint+" message="+mesg);		
							
System.err.println("RECEIVED MESG="+mesg+" for endpoint="+ep+" dispatcher="+dispatcher);
							boolean handled=false;
							//int retry=0;
							//do {
							try {
								synchronized(this) {
									wait(1000);
								}
							} catch(Exception e) {
							}
System.err.println("PROCESS AFTER DELAY MESG="+mesg+" for endpoint="+ep+" dispatcher="+dispatcher);
							
								handled = dispatcher.onMessage(ep, mesg);
								/*
								if (handled == false) {
									retry++;
									try {
										synchronized(this) {
											wait(20);
										}
									} catch(Exception e) {
									}
								}
							} while (handled==false && retry < 10);
							*/
								
							if (handled == false) {
								System.err.println("GPB: JMS RECEIVE MESSAGE NOT HANDLED="+mesg);
								
								// TODO: For now acknowledge anyway.....
								//arg0.acknowledge();
							} else {
								arg0.acknowledge();
								System.err.println("GPB: JMS RECEIVE ACKED: "+new java.util.Date()+" ("+Thread.currentThread()+"): endpoint="+endpoint+" message="+mesg);		
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			});
		
			m_consumers.put(endpoint.getName(), receiver);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected byte[] serialize(Endpoint endpoint) {
		byte[] ret=null;
		
		try {
			java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
			java.io.ObjectOutputStream oos=new java.io.ObjectOutputStream(baos);
			
			oos.writeObject(endpoint);
			
			oos.close();
			baos.close();
			
			ret = baos.toByteArray();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return(ret);
	}
	
	protected Endpoint deserialize(byte[] b) {
		Endpoint ret=null;
		
		try {
			java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(b);
			java.io.ObjectInputStream ois=new java.io.ObjectInputStream(bais);
			
			ret = (Endpoint)ois.readObject();
			
			ois.close();
			bais.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return(ret);
	}
	
	protected String getQueueName(Endpoint endpoint) {
		return("dynamicQueue/"+endpoint.getName());
	}

	@Override
	public void unregister(Endpoint endpoint) {
		javax.jms.MessageConsumer consumer=m_consumers.remove(endpoint.getName());
		
		try {
			consumer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send(Endpoint endpoint, Message message) {
		
		if (_log.isLoggable(Level.FINEST)) {
			_log.info("SEND["+endpoint+"]: "+message.getValue());
		}
//System.err.println("GPB: JMS SEND ("+Thread.currentThread()+"): endpoint="+endpoint+" message="+message);		
		
		// TODO: Maybe create producer before, and just supplied queue with send
		try {
			javax.jms.Queue queue=session.createQueue(getQueueName(endpoint));
			javax.jms.MessageProducer producer=session.createProducer(queue);
			
			if (message instanceof DefaultMessage) {
				((DefaultMessage)message).setDestination(endpoint);
			}
			
			javax.jms.Message mesg=session.createObjectMessage(message);
			
//System.err.println("SENDING MESG="+message+" to endpoint="+endpoint);			
			producer.send(mesg);
			
			producer.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
