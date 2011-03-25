package example.purchasing.broker;
import org.scribble.runtime.*;
import org.scribble.runtime.messaging.*;
import example.purchasing.data.*;
import example.purchasing.util.PurchasingUtil;
public class PurchasingBroker_broker extends DefaultProcess {

	public static String PROCESS_NAME="PurchasingBroker_broker";

	public PurchasingBroker_broker(Endpoint endpoint) {
		super(PROCESS_NAME, endpoint);
		getTasks().add(new init(null,null));
	}

	public class init extends DefaultTask {
		public Endpoint user;
		public java.util.List<Quote> quoteList;
		public example.purchasing.data.OrderConfirmed orderConfirmed;
		public Order placeOrder;
		public example.purchasing.data.AcceptQuote acceptQuote;
		public example.purchasing.data.InvalidProduct invProd;
		public example.purchasing.data.CancelQuote cancelQuote;
		public example.purchasing.data.RequestForQuote rfq;
		public int supplierCount;

		public init(DefaultTask parent,Endpoint user) {
			super(parent);
			this.user = user;
		}
		public boolean onMessage(Context context, Message mesg) {
			boolean ret=false;
			started(context);
			quoteList = new java.util.Vector<Quote>();
			placeOrder = null;
			supplierCount = 0;
			// user -> [broker:rfq]
			if (mesg == null || (mesg.getValue() instanceof example.purchasing.data.RequestForQuote) == false ||
					(user != null && user.getChannelId() != null &&
							mesg.getSource().getChannelId() != null &&
							user.getChannelId().equals(mesg.getSource().getChannelId())==false)) {
				return(false);
			}
			rfq = (example.purchasing.data.RequestForQuote)mesg.getValue();
			user = mesg.getSource();
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
				if (!PurchasingUtil.isValidProduct(rfq.productCode)) {
					context.send(user, invProd);
				} else {
					T3 recur=new T3(null);
					T4 postrecur=new T4(this);
					recur.scheduleOnCompletion(postrecur);
					if (recur.onMessage(context,mesg)) {
						ret = true;
						mesg = null;
					}
					if (recur.isCompleted() == false) {
						getTasks().add(recur);
					}
				}
				completed(context);
				return(ret);
			}
			public class T3 extends DefaultTask {

				public T3(DefaultTask parent) {
					super(parent);
				}
				public boolean onMessage(Context context, Message mesg) {
					boolean ret=false;
					started(context);
					T5 cur=new T5(null);
					T6 post=new T6(this);
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
				public class T5 extends DefaultTask {
					public Endpoint supplier;

					public T5(DefaultTask parent) {
						super(parent);
					}
					public boolean onMessage(Context context, Message mesg) {
						boolean ret=false;
						started(context);
						if (supplierCount<PurchasingUtil.supplierList.size()) {
							java.util.Properties supplierProps=new java.util.Properties();
							supplierProps.put("supplierId",PurchasingUtil.supplierList.get(supplierCount));
							supplier = context.find("example.RFQ@Supplier",supplierProps);
							context.send(supplier, rfq);
							T7 postpar=new T7(this);
							T8 pathtask0=new T8(null);
							pathtask0.scheduleOnCompletion(postpar);
							T9 pathtask1=new T9(null);
							pathtask1.scheduleOnCompletion(postpar);
							if (pathtask0.onMessage(context,mesg)) {
								ret = true;
								mesg = null;
							}
							if (pathtask0.isCompleted() == false) {
								getTasks().add(pathtask0);
							}
							if (pathtask1.onMessage(context,mesg)) {
								ret = true;
								mesg = null;
							}
							if (pathtask1.isCompleted() == false) {
								getTasks().add(pathtask1);
							}
						}
						completed(context);
						return(ret);
					}
					public class T7 extends DefaultTask {

						public T7(DefaultTask parent) {
							super(parent);
						}
						public boolean onMessage(Context context, Message mesg) {
							boolean ret=false;
							started(context);
							completed(context);
							return(ret);
						}
					}
					public class T8 extends DefaultTask {

						public T8(DefaultTask parent) {
							super(parent);
						}
						public boolean onMessage(Context context, Message mesg) {
							boolean ret=false;
							started(context);
							supplierCount=supplierCount+1;
							T3 recur=new T3(null);
							T10 postrecur=new T10(this);
							recur.scheduleOnCompletion(postrecur);
							if (recur.onMessage(context,mesg)) {
								ret = true;
								mesg = null;
							}
							if (recur.isCompleted() == false) {
								getTasks().add(recur);
							}
							completed(context);
							return(ret);
						}
						public class T10 extends DefaultTask {

							public T10(DefaultTask parent) {
								super(parent);
							}
							public boolean onMessage(Context context, Message mesg) {
								boolean ret=false;
								started(context);
								completed(context);
								return(ret);
							}
						}
					}
					public class T9 extends DefaultTask {
						public Quote quote;

						public T9(DefaultTask parent) {
							super(parent);
						}
						public boolean onMessage(Context context, Message mesg) {
							boolean ret=false;
							started(context);
							quote = null;
							// supplier -> [broker:quote]
							if (mesg == null || (mesg.getValue() instanceof Quote) == false ||
									(supplier != null && supplier.getChannelId() != null &&
											mesg.getSource().getChannelId() != null &&
											supplier.getChannelId().equals(mesg.getSource().getChannelId())==false)) {
								return(false);
							}
							quote = (Quote)mesg.getValue();
							supplier = mesg.getSource();
							ret = true;
							mesg = null;
							quoteList.add(quote);
							completed(context);
							return(ret);
						}
					}
				}
				public class T6 extends DefaultTask {

					public T6(DefaultTask parent) {
						super(parent);
					}
					public boolean onMessage(Context context, Message mesg) {
						boolean ret=false;
						started(context);
						completed(context);
						return(ret);
					}
				}
			}
			public class T4 extends DefaultTask {
				public Quote bestQuote;

				public T4(DefaultTask parent) {
					super(parent);
				}
				public boolean onMessage(Context context, Message mesg) {
					boolean ret=false;
					started(context);
					bestQuote = (quoteList.size()>0?quoteList.get(0):null);
					T11 cur=new T11(null);
					T12 post=new T12(this);
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
				public class T11 extends DefaultTask {

					public T11(DefaultTask parent) {
						super(parent);
					}
					public boolean onMessage(Context context, Message mesg) {
						boolean ret=false;
						started(context);
						if (bestQuote != null) {
							context.send(user, bestQuote);
							MutuallyExclusiveTasks choicetask=new MutuallyExclusiveTasks();
							T13 postchoice=new T13(this);
							choicetask.scheduleOnCompletion(postchoice);
							choicetask.addTask(new T14(null));
							choicetask.addTask(new T15(null));
							if (choicetask.onMessage(context,mesg)) {
								ret = true;
								mesg = null;
							}
							if (choicetask.isCompleted() == false) {
								getTasks().add(choicetask);
							}
						}
						completed(context);
						return(ret);
					}
					public class T13 extends DefaultTask {

						public T13(DefaultTask parent) {
							super(parent);
						}
						public boolean onMessage(Context context, Message mesg) {
							boolean ret=false;
							started(context);
							completed(context);
							return(ret);
						}
					}
					public class T14 extends DefaultTask {
						public Endpoint creditAgency;
						public CreditReport report;

						public T14(DefaultTask parent) {
							super(parent);
						}
						public boolean onMessage(Context context, Message mesg) {
							boolean ret=false;
							started(context);
							// user -> [broker:acceptQuote]
							if (mesg == null || (mesg.getValue() instanceof example.purchasing.data.AcceptQuote) == false ||
									(user != null && user.getChannelId() != null &&
											mesg.getSource().getChannelId() != null &&
											user.getChannelId().equals(mesg.getSource().getChannelId())==false)) {
								return(false);
							}
							acceptQuote = (example.purchasing.data.AcceptQuote)mesg.getValue();
							user = mesg.getSource();
							ret = true;
							mesg = null;
							creditAgency = example.purchasing.broker.PurchasingBroker_creditAgency_factory.instance().getEndpoint();
							context.send(creditAgency, (new CreditCheck(bestQuote.getAmount())));
							T16 intcall=new T16(this);
							if (intcall.onMessage(context,mesg)) {
								ret = true;
								mesg = null;
							}
							if (intcall.isCompleted() == false) {
								getTasks().add(intcall);
							}
							completed(context);
							return(ret);
						}
						public class T16 extends DefaultTask {

							public T16(DefaultTask parent) {
								super(parent);
							}
							public boolean onMessage(Context context, Message mesg) {
								boolean ret=false;
								started(context);
								// creditAgency -> [broker:report]
								if (mesg == null || (mesg.getValue() instanceof CreditReport) == false ||
										(creditAgency != null && creditAgency.getChannelId() != null &&
												mesg.getSource().getChannelId() != null &&
												creditAgency.getChannelId().equals(mesg.getSource().getChannelId())==false)) {
									return(false);
								}
								report = (CreditReport)mesg.getValue();
								creditAgency = mesg.getSource();
								ret = true;
								mesg = null;
								T17 cur=new T17(null);
								T18 post=new T18(this);
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
							public class T17 extends DefaultTask {
								public Endpoint supplier;

								public T17(DefaultTask parent) {
									super(parent);
								}
								public boolean onMessage(Context context, Message mesg) {
									boolean ret=false;
									started(context);
									if (report.isCreditOk()) {
										java.util.Properties supplierProps=new java.util.Properties();
										supplierProps.put("supplierId",PurchasingUtil.supplierList.get(acceptQuote.supplierId));
										supplier = context.find("example.RFQ@Supplier",supplierProps);
										placeOrder=new Order();
										context.send(supplier, placeOrder);
										T19 intcall=new T19(this);
										if (intcall.onMessage(context,mesg)) {
											ret = true;
											mesg = null;
										}
										if (intcall.isCompleted() == false) {
											getTasks().add(intcall);
										}
									} else {
										context.send(user, new InvalidCredit());
									}
									completed(context);
									return(ret);
								}
								public class T19 extends DefaultTask {

									public T19(DefaultTask parent) {
										super(parent);
									}
									public boolean onMessage(Context context, Message mesg) {
										boolean ret=false;
										started(context);
										// supplier -> [broker:orderConfirmed]
										if (mesg == null || (mesg.getValue() instanceof example.purchasing.data.OrderConfirmed) == false ||
												(supplier != null && supplier.getChannelId() != null &&
														mesg.getSource().getChannelId() != null &&
														supplier.getChannelId().equals(mesg.getSource().getChannelId())==false)) {
											return(false);
										}
										orderConfirmed = (example.purchasing.data.OrderConfirmed)mesg.getValue();
										supplier = mesg.getSource();
										ret = true;
										mesg = null;
										context.send(user, orderConfirmed);
										completed(context);
										return(ret);
									}
								}
							}
							public class T18 extends DefaultTask {

								public T18(DefaultTask parent) {
									super(parent);
								}
								public boolean onMessage(Context context, Message mesg) {
									boolean ret=false;
									started(context);
									completed(context);
									return(ret);
								}
							}
						}
					}
					public class T15 extends DefaultTask {

						public T15(DefaultTask parent) {
							super(parent);
						}
						public boolean onMessage(Context context, Message mesg) {
							boolean ret=false;
							started(context);
							// user -> [broker:cancelQuote]
							if (mesg == null || (mesg.getValue() instanceof example.purchasing.data.CancelQuote) == false ||
									(user != null && user.getChannelId() != null &&
											mesg.getSource().getChannelId() != null &&
											user.getChannelId().equals(mesg.getSource().getChannelId())==false)) {
								return(false);
							}
							cancelQuote = (example.purchasing.data.CancelQuote)mesg.getValue();
							user = mesg.getSource();
							ret = true;
							mesg = null;
							completed(context);
							return(ret);
						}
					}
				}
				public class T12 extends DefaultTask {

					public T12(DefaultTask parent) {
						super(parent);
					}
					public boolean onMessage(Context context, Message mesg) {
						boolean ret=false;
						started(context);
						completed(context);
						return(ret);
					}
				}
			}
		}
		public class T2 extends DefaultTask {

			public T2(DefaultTask parent) {
				super(parent);
			}
			public boolean onMessage(Context context, Message mesg) {
				boolean ret=false;
				started(context);
				completed(context);
				return(ret);
			}
		}
	}

}

