namespace example.purchasing.broker.PurchasingBroker;

// How to reference a type for the actor? Possibly using the actor type?

// Is there a difference between an abstract actor (which is essentially an
// interface) and another party that was part of the same global description when
// it comes to locating them in the endpoint code?

// Need to find a better way to store the selected supplier. It needs to be cached at the
// broker and not in the message sent back to the user for it to be returned in the
// acceptQuote message.

import example.purchasing.data.*;
import example.purchasing.util.PurchasingUtil;

requires resource database="SupplierDB";	// Gets converted to a constraint on the runtime,
											// possibly still stored in manifest entry
requires module name="org.acme.BrokerUtil", version="1.0.0";	// Gets converted to bundle 
																// requirement in manifest
init(user) {
	actor broker;

	example.purchasing.data.RequestForQuote broker:rfq;
	example.purchasing.data.AcceptQuote broker:acceptQuote;
	example.purchasing.data.CancelQuote broker:cancelQuote;
	example.purchasing.data.OrderConfirmed broker:orderConfirmed;
	example.purchasing.data.InvalidProduct broker:invProd;

	java.util.List<Quote> broker:quoteList=new java.util.Vector<Quote>();
	
	Order broker:placeOrder=null;
	int broker:supplierCount=0;

	user -> broker:rfq;

	if (broker:!PurchasingUtil.isValidProduct(rfq.productCode)) {
		broker:invProd -> user;
	} else {
		contactSupplier: {

			if (broker:supplierCount < PurchasingUtil.supplierList.size()) {
				// TODO: If local, then would not have parameters, and
				// possibly have an optional 'type' field to represent the
				// local type - then used by the local client to lookup the
				// local server? So 'implements' and 'with' are used to locate
				// a remote server, 'implements' is used by a remove server to
				// register itself against the protocol, and 'type' is used by
				// local client/server to locate/register locally.
				 
				actor supplier implements example.RFQ@Supplier
						with supplierId=PurchasingUtil.supplierList.get(supplierCount);
				
				// TODO: The supplier endpoint in the broker should have a unique id which
				// is sent as a 'replyTo' field in the message, and used to correlate the
				// response back to the existing supplier object (if it exists).
				
				broker:rfq -> supplier;

				par {
					// Need to increment the supplierCount
					broker:supplierCount = supplierCount+1;
					
					contactSupplier;
				} and {
					// Issue: How do we know where the quote came from?
					// Should there be some check on supplier id/endpoint/replyToId??
					Quote broker:quote=null; 
					
					supplier -> broker:quote;
					
					// Add to quote list
					broker:quoteList.add(quote);
					
					// TODO: Need to map quote ref to supplier id
				}
			}
		}
		
		Quote broker:bestQuote = (quoteList.size() > 0 ? quoteList.get(0) : null);

		if (broker:bestQuote != null) {

			broker:bestQuote -> user;

			choice {
				user -> broker:acceptQuote {
					actor creditAgency;
					
					broker:(new CreditCheck(bestQuote.getAmount()))->creditAgency;
					
					CreditReport broker:report;
										
					creditAgency -> broker:report;
					
					if (broker:report.isCreditOk()) {
						actor supplier implements example.RFQ@Supplier
								with supplierId=PurchasingUtil.supplierList.get(acceptQuote.supplierId);
	
						broker:placeOrder = new Order();
						
						broker:placeOrder -> supplier;
	
						supplier -> broker:orderConfirmed;
	
						broker:orderConfirmed -> user;
					} else {
						broker:new InvalidCredit() -> user;
					}
				}
				user -> broker:cancelQuote {
				}
			}
		}
	}
}
