package example.purchasing.broker;
import org.scribble.runtime.*;
import org.scribble.runtime.messaging.*;
public class PurchasingBroker_creditAgency_factory implements ProcessFactory {

	private static PurchasingBroker_creditAgency_factory m_instance=new PurchasingBroker_creditAgency_factory();

	public static PurchasingBroker_creditAgency_factory instance() {
		return(m_instance);
	}

	public org.scribble.runtime.Process createProcess() {
		return(new PurchasingBroker_creditAgency(new Endpoint(getName().toString(),java.util.UUID.randomUUID())));
	}

	public String getName() {
		return PurchasingBroker_creditAgency.PROCESS_NAME;
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
