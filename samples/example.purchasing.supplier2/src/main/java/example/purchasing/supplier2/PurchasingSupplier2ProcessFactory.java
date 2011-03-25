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
package example.purchasing.supplier2;

import org.scribble.runtime.Process;
import org.scribble.runtime.ProcessFactory;
import org.scribble.runtime.messaging.Endpoint;

public class PurchasingSupplier2ProcessFactory implements ProcessFactory {

	private static java.util.Set<String> m_implements=
		new java.util.HashSet<String>();

	static {
		//m_implements.add("example.Supplier");
		m_implements.add("example.RFQ@Supplier");
	}

	public Process createProcess() {
		//return(new PurchasingSupplier2(new Endpoint(getName().toString(),
		//		java.util.UUID.randomUUID())));
		return(new PurchasingSupplier2_supplier(new Endpoint(getName().toString(),
				java.util.UUID.randomUUID())));
	}

	public String getName() {
		//return PurchasingSupplier2.PROCESS_NAME;
		return PurchasingSupplier2_supplier.PROCESS_NAME;
	}

	/**
	 * This method returns the properties that distinguish this
	 * implementation of the process name.
	 * 
	 * @return The optional process specific properties
	 */
	public java.util.Properties getProperties() {
		return(PurchasingSupplier2.PROCESS_PROPERTIES);
	}
	
	/**
	 * This method returns the set of process names that
	 * this process implements.
	 * 
	 * @return The set of implemented process names
	 */
	public java.util.Set<String> getImplements() {
		return(m_implements);
	}
	
	public Endpoint getEndpoint() {
		return(new Endpoint(getName().toString(), null));
	}

}
