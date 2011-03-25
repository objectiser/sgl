namespace example.purchasing.supplier2.PurchasingSupplier2;

import example.purchasing.data.*;
import example.purchasing.util.PurchasingUtil;

requires resource database="SupplierDB";	// Gets converted to a constraint on the runtime,
											// possibly still stored in manifest entry
requires module name="org.acme.BrokerUtil", version="1.0.0";	// Gets converted to bundle 
																// requirement in manifest
init(broker) {
	actor supplier implements example.RFQ@Supplier;

	example.purchasing.data.RequestForQuote broker:rfq;

	broker -> supplier:rfq;

	supplier:new Quote() -> broker;
}
