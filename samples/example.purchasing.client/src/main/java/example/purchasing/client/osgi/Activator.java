package example.purchasing.client.osgi;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.scribble.runtime.messaging.LocalMessagingLayer;
import org.scribble.runtime.messaging.MessagingLayer;

public class Activator implements BundleActivator {

	private static BundleContext context;
	
	private LocalMessagingLayer m_messagingLayer=null;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		Properties props=new Properties();
		
        m_messagingLayer = new LocalMessagingLayer();
		
		context.registerService(MessagingLayer.class.getName(), 
				m_messagingLayer, props);
		
		example.purchasing.client.Client client=
					new example.purchasing.client.Client(null, m_messagingLayer);
		
		Thread t=new Thread(client);
		t.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
}
