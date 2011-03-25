package example.purchasing.supplier1;
import org.scribble.runtime.*;
import org.scribble.runtime.messaging.*;
import example.purchasing.data.*;
import example.purchasing.util.PurchasingUtil;
public class PurchasingSupplier1_supplier extends DefaultProcess {

	public static String PROCESS_NAME="PurchasingSupplier1_supplier";

	public PurchasingSupplier1_supplier(Endpoint endpoint) {
		super(PROCESS_NAME, endpoint);
		getTasks().add(new requestForQuote(null,null));
	}

	public class requestForQuote extends DefaultTask {
		public Endpoint broker;
		public example.purchasing.data.Order placeOrder;
		public example.purchasing.data.RequestForQuote rfq;

		public requestForQuote(DefaultTask parent,Endpoint broker) {
			super(parent);
			this.broker = broker;
		}
		public boolean onMessage(Context context, Message mesg) {
			boolean ret=false;
			started(context);
			MutuallyExclusiveTasks choicetask=new MutuallyExclusiveTasks();
			T1 postchoice=new T1(this);
			choicetask.scheduleOnCompletion(postchoice);
			choicetask.addTask(new T2(null));
			choicetask.addTask(new T3(null));
			if (choicetask.onMessage(context,mesg)) {
				ret = true;
				mesg = null;
			}
			if (choicetask.isCompleted() == false) {
				getTasks().add(choicetask);
			}
			completed(context);
			return(ret);
		}
		public class T1 extends DefaultTask {

			public T1(DefaultTask parent) {
				super(parent);
			}
			public boolean onMessage(Context context, Message mesg) {
				boolean ret=false;
				started(context);
				completed(context);
				return(ret);
			}
		}
		public class T2 extends DefaultTask {
			public Quote quote = new Quote();

			public T2(DefaultTask parent) {
				super(parent);
			}
			public boolean onMessage(Context context, Message mesg) {
				boolean ret=false;
				started(context);
				// broker -> [supplier.rfq]
				if (mesg == null || (mesg.getValue() instanceof example.purchasing.data.RequestForQuote) == false ||
						(broker != null && broker.getChannelId() != null &&
								mesg.getSource().getChannelId() != null &&
								broker.getChannelId().equals(mesg.getSource().getChannelId())==false)) {
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
		public class T3 extends DefaultTask {
			public OrderConfirmed orderConfirmed = new OrderConfirmed();

			public T3(DefaultTask parent) {
				super(parent);
			}
			public boolean onMessage(Context context, Message mesg) {
				boolean ret=false;
				started(context);
				// broker -> [supplier.placeOrder]
				if (mesg == null || (mesg.getValue() instanceof example.purchasing.data.Order) == false ||
						(broker != null && broker.getChannelId() != null &&
								mesg.getSource().getChannelId() != null &&
								broker.getChannelId().equals(mesg.getSource().getChannelId())==false)) {
					return(false);
				}
				placeOrder = (example.purchasing.data.Order)mesg.getValue();
				broker = mesg.getSource();
				ret = true;
				mesg = null;
				context.send(broker, orderConfirmed);
				completed(context);
				return(ret);
			}
		}
	}

}
