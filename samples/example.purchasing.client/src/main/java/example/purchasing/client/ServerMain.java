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

import org.scribble.runtime.manager.infinispan.InfinispanProcessManager;
import org.scribble.runtime.manager.inmemory.InMemoryProcessManager;
import org.scribble.runtime.messaging.LocalMessagingLayer;
import org.scribble.runtime.messaging.MessagingLayer;
import org.scribble.runtime.messaging.activemq.ActiveMQMessagingLayer;
import org.scribble.runtime.registry.ProcessRegistry;
import org.scribble.runtime.registry.infinispan.InfinispanProcessRegistry;
import org.scribble.runtime.registry.inmemory.InMemoryProcessRegistry;

import example.purchasing.broker.PurchasingBroker_broker_factory;
import example.purchasing.broker.PurchasingBroker_creditAgency_factory;
import example.purchasing.supplier1.PurchasingSupplier1ProcessFactory;
import example.purchasing.supplier2.PurchasingSupplier2ProcessFactory;

public class ServerMain {

	public static void main(String[] args) {
		
		//InMemoryProcessManager pm=new InMemoryProcessManager();
		InfinispanProcessManager pm=new InfinispanProcessManager();
		
		MessagingLayer ml=new ActiveMQMessagingLayer();
		//MessagingLayer ml=new LocalMessagingLayer();
		pm.setMessagingLayer(ml);
		
		ProcessRegistry registry=new InfinispanProcessRegistry();
		pm.setProcessRegistry(registry);
		
		// Add process factories
		PurchasingBroker_broker_factory factory1=new PurchasingBroker_broker_factory();
		pm.register(factory1);
		
		PurchasingSupplier1ProcessFactory factory2=new PurchasingSupplier1ProcessFactory();
		pm.register(factory2);
		
		PurchasingSupplier2ProcessFactory factory3=new PurchasingSupplier2ProcessFactory();
		pm.register(factory3);
		
		PurchasingBroker_creditAgency_factory factory4=new PurchasingBroker_creditAgency_factory();
		pm.register(factory4);
		
		System.out.println("FIND: "+registry.find(factory1.getName(), null));
		
		try {
			System.out.println("WAIT FOR KEY .....");
			System.in.read();
			System.exit(0);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
