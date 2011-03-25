package example.purchasing.broker;
import org.scribble.runtime.*;
import org.scribble.runtime.messaging.*;
public class PurchasingBroker_broker_factory implements ProcessFactory {

	private static PurchasingBroker_broker_factory m_instance=new PurchasingBroker_broker_factory();

	public static PurchasingBroker_broker_factory instance() {
		return(m_instance);
	}

	public org.scribble.runtime.Process createProcess() {
		return(new PurchasingBroker_broker(new Endpoint(getName().toString(),java.util.UUID.randomUUID())));
	}

	public String getName() {
		return PurchasingBroker_broker.PROCESS_NAME;
	}

	public java.util.Properties getProperties() {
		return(null);
	}

	public java.util.Set<String> getImplements() {
		return(java.util.Collections.EMPTY_SET);
	}

	public Endpoint getEndpoint() {
		return(new Endpoint(getName().toString(),null));
	}
}
