package example.purchasing.broker;
import org.scribble.runtime.*;
import org.scribble.runtime.messaging.*;
import example.purchasing.data.*;
import example.purchasing.util.PurchasingUtil;
public class PurchasingBroker_creditAgency extends DefaultProcess {

	public static String PROCESS_NAME="PurchasingBroker_creditAgency";

	public PurchasingBroker_creditAgency(Endpoint endpoint) {
		super(PROCESS_NAME, endpoint);
		getTasks().add(new init(null,null));
	}

	public class init extends DefaultTask {
		public Endpoint broker;
		public CreditCheck check;
		public CreditReport report;

		public init(DefaultTask parent,Endpoint broker) {
			super(parent);
			this.broker = broker;
		}
		public boolean onMessage(Context context, Message mesg) {
			boolean ret=false;
			started(context);
			// broker -> [creditAgency:check]
			if (mesg == null || (mesg.getValue() instanceof CreditCheck) == false ||
					(broker != null && broker.getChannelId() != null &&
							mesg.getSource().getChannelId() != null &&
							broker.getChannelId().equals(mesg.getSource().getChannelId())==false)) {
				return(false);
			}
			check = (CreditCheck)mesg.getValue();
			broker = mesg.getSource();
			ret = true;
			mesg = null;
			T1 cur=new T1(null);
			T2 post=new T2(this);
			cur.scheduleOnCompletion(post);
			if (cur.onMessage(context,mesg)) {
				ret = true;
				mesg = null;
			}
			if (cur.isCompleted() == false) {
				getTasks().add(cur);
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
				if (check.getAmount()== 0) {
					report=new CreditReport(false);
				} else {
					report=new CreditReport(true);
				}
				completed(context);
				return(ret);
			}
		}
		public class T2 extends DefaultTask {

			public T2(DefaultTask parent) {
				super(parent);
			}
			public boolean onMessage(Context context, Message mesg) {
				boolean ret=false;
				started(context);
				context.send(broker, report);
				completed(context);
				return(ret);
			}
		}
	}

}

