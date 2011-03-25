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
init(broker) {
	actor creditAgency;
	
	CreditCheck creditAgency:check;
	
	broker -> creditAgency:check;
	
	CreditReport creditAgency:report;
	
	if (creditAgency:check.getAmount() == 0) {
		// NOTE: If broker/supplier activity put in here, then would need to
		// report error as there is no way for broker to know the decision
		// at the creditAgency, unless the activities are preceded by
		// a message (in each path) which can be used at the broker
		// to distinguish path taken(?) - but then choice should be used?
		creditAgency:report = new CreditReport(false);
	} else {
		creditAgency:report = new CreditReport(true);
	}
	
	creditAgency:report -> broker;
}
