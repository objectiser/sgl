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
package example.purchasing.client;

import org.scribble.runtime.MessageListener;
import org.scribble.runtime.Process;
import org.scribble.runtime.ScribbleRuntime;
import org.scribble.runtime.messaging.DefaultMessage;
import org.scribble.runtime.messaging.Endpoint;
import org.scribble.runtime.messaging.LocalMessagingLayer;
import org.scribble.runtime.messaging.Message;
import org.scribble.runtime.messaging.MessageDispatcher;
import org.scribble.runtime.messaging.MessagingLayer;
import org.scribble.runtime.manager.AbstractProcessManager;

import example.purchasing.broker.PurchasingBroker_broker;
import example.purchasing.data.AcceptQuote;
import example.purchasing.data.InvalidCredit;
import example.purchasing.data.OrderConfirmed;
import example.purchasing.data.Quote;
import example.purchasing.data.RequestForQuote;
import example.purchasing.util.PurchasingUtil;

public class Client implements Runnable {

	private AbstractProcessManager m_processManager=null;
	private MessagingLayer m_messagingLayer=null;
	private Endpoint m_brokerEndpoint=null;
	private MessageDispatcher m_brokerDispatcher=null;
	private Endpoint m_userEndpoint=new Endpoint("User", null);
	private ScribbleRuntime m_runtime=null;
	
	public Client(AbstractProcessManager pm, MessagingLayer ml) {
		m_processManager = pm;
		m_messagingLayer = ml;
		m_runtime = new ScribbleRuntime(pm, ml);
	}
	
	public void run() {
		
		try {
			synchronized(this) {
				wait(2000);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		long startTime=System.currentTimeMillis();
		int num=1;	// 100 in 4532ms,4192ms
						// 100 full process (with accept/confirm), 12500ms
						// 100 full process with infinispan 12650ms
		
		//WaitForProcessCompleted waiter=new WaitForProcessCompleted();
		//m_processManager.setProcessManagerListener(waiter);
		//m_runtime.setProcessManagerListener(waiter);
		
		java.util.Properties brokerProps=new java.util.Properties();
		
		//final org.scribble.runtime.Client broker=
		//	m_runtime.createClient("PurchasingBroker_broker",brokerProps);

		for (int i=0; i < num; i++) {
			final int ind=i;
			final org.scribble.runtime.Client broker=
				m_runtime.createClient("PurchasingBroker_broker",brokerProps);

			// Send request for quote
			RequestForQuote rfq=new RequestForQuote();
			rfq.setProductCode("pc1");
			
			broker.setMessageListener(new MessageListener() {
				public void onMessage(Message msg) {		
System.out.println("RECEIVED("+ind+"): "+msg.getValue());					
					if (msg.getValue() instanceof Quote) {
						AcceptQuote accept=new AcceptQuote();
						broker.send(accept);
					} else if (msg.getValue() instanceof OrderConfirmed) {

						// Close the client
						broker.close();
					} else if (msg.getValue() instanceof InvalidCredit) {

						// Close the client
						broker.close();
					}
				}
			});
			
			broker.send(rfq);

			if (broker.waitForClose(10000) == false) {
				System.err.println("Client was not closed");
			}
			/*
			synchronized(waiter) {
				
				// TODO: Need a synchronous, or callback API for clients, to enable them
				// to block awaiting a message.
				try {
					waiter.waitForCompletion();
					
					//System.err.println("COMPLETED, then wait for 5 secs");
					
					//waiter.wait(2000);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			*/		
		}
		
		long endTime=System.currentTimeMillis();
		
		System.err.println("********************************\r\nTook: "+(endTime-startTime)+"ms for "+num+" iterations");
	}
	
	/*
	private class WaitForProcessCompleted implements ProcessManagerListener {

		public void waitForCompletion() throws Exception {
			wait(10000);
		}
		
		public void onProcessCompleted(Process process) {
			if (process.getName().equals(PurchasingBroker_broker.PROCESS_NAME)) {
			//if (process.getName().equals(PurchasingBroker.PROCESS_NAME)) {
				synchronized(this) {
					notifyAll();
				}
			}
		}	
	}
	*/
}
