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
package example.purchasing.supplier1;

import org.scribble.runtime.Context;
import org.scribble.runtime.DefaultProcess;
import org.scribble.runtime.DefaultTask;
import org.scribble.runtime.messaging.Endpoint;
import org.scribble.runtime.messaging.Message;

import example.purchasing.data.*;

public class PurchasingSupplier1 extends DefaultProcess {

	private static final long serialVersionUID = 4126310944196068348L;

	public static java.util.List<String> validCodes=new java.util.Vector<String>();
	public static java.util.List<String> supplierList=new java.util.Vector<String>();
	
	public static String PROCESS_NAME="RequestForQuote-Supplier1";
	public static java.util.Properties PROCESS_PROPERTIES=new java.util.Properties();

	private static final java.util.logging.Logger _log=
					java.util.logging.Logger.getLogger(PurchasingSupplier1.class.getName());
	
	static {
		validCodes.add("pc1");
		validCodes.add("pc2");
		
		supplierList.add("supplier1");
		supplierList.add("supplier2");
		
		PROCESS_PROPERTIES.put("supplierId", "supplier1");
	}
	
	public RequestForQuote rfq;
	public java.util.List<Quote> quoteList=new java.util.Vector<Quote>();
	public Endpoint user;
	public int supplierCount;
	
	public PurchasingSupplier1(Endpoint endpoint) {
		super(PROCESS_NAME, endpoint);
		
		getTasks().add(new Initializer(null));
	}
		
	public boolean isValidProduct(String code) {
		return(validCodes.contains(code));
	}
	
	public class Initializer extends DefaultTask {
		
		public Initializer(DefaultTask parent) {
			super(parent);
		}

		public boolean onMessage(Context context, Message mesg) {
			boolean ret=false;
			
			started(context);
			
			if (mesg != null && mesg.getValue() instanceof RequestForQuote) {
				rfq = (RequestForQuote)mesg.getValue();
				
				user = mesg.getSource();
				
				ret = true;
				mesg = null;

				Quote quote=new Quote();
					
				context.send(user, quote);
				
				completed(context);
			}
			
			return(ret);
		}
	}
}
