package example.purchasing.supplier2;
import org.scribble.runtime.*;
import org.scribble.runtime.messaging.*;
import example.purchasing.data.*;
import example.purchasing.util.PurchasingUtil;
public class PurchasingSupplier2_supplier extends DefaultProcess {

	public static String PROCESS_NAME="PurchasingSupplier2_supplier";

	public PurchasingSupplier2_supplier(Endpoint endpoint) {
		super(PROCESS_NAME, endpoint);
		getTasks().add(new requestForQuote(null,null));
	}

	public class requestForQuote extends DefaultTask {
		public Endpoint broker;
		public Quote quote = new Quote();
		public example.purchasing.data.RequestForQuote rfq;

		public requestForQuote(DefaultTask parent,Endpoint broker) {
			super(parent);
			this.broker = broker;
		}
		public boolean onMessage(Context context, Message mesg) {
			boolean ret=false;
			started(context);
			// broker -> [supplier.rfq]
			if (mesg == null || (mesg.getValue() instanceof example.purchasing.data.RequestForQuote) == false ||
					(broker != null && broker.getChannelId() != null &&
							mesg.getSource().getChannelId() != null &&
							broker.getChannelId().equals(mesg.getSource().getChannelId()))) {
				return(false);
			}
			rfq = (example.purchasing.data.RequestForQuote)mesg.getValue();
			broker = mesg.getSource();
			ret = true;
			mesg = null;
			context.send(broker, quote);
			completed(context);
			return(ret);
		}
	}

}
