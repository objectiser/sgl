package org.scribble.runtime.manager.infinispan.osgi;

import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.scribble.runtime.ProcessFactory;
import org.scribble.runtime.manager.ProcessManager;
import org.scribble.runtime.manager.infinispan.InfinispanProcessManager;
import org.scribble.runtime.messaging.MessagingLayer;

public class Activator implements BundleActivator {

	private static final java.util.logging.Logger _log=
		java.util.logging.Logger.getLogger(Activator.class.getName());
	
	private static BundleContext context;
	private org.osgi.util.tracker.ServiceTracker m_processFactoryTracker=null;
	private org.osgi.util.tracker.ServiceTracker m_messagingLayerTracker=null;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		URL url=Activator.class.getResource("/cache_config.xml");
		System.out.println("URL="+url);
		
        Properties props = new Properties();

        // Register the Process Manager
		final InfinispanProcessManager pm=new InfinispanProcessManager();
		
		context.registerService(ProcessManager.class.getName(), 
					pm, props);
		
		// Check for existing process factories
		/*
		ServiceReference[] refs=
			context.getAllServiceReferences(ProcessFactory.class.getName(), null);
		
		if (refs != null) {
			for (ServiceReference sr : refs) {
				Object service=context.getService(sr);
				
				System.out.println("REGISTER1: "+service);
				pm.register((ProcessFactory)service);
			}
		}
		*/

		// Register listener for new process factories
		m_processFactoryTracker = new ServiceTracker(context,
				ProcessFactory.class.getName(), null) {

			public Object addingService(ServiceReference ref) {
				Object ret=super.addingService(ref);
				
				if (_log.isLoggable(Level.INFO)) {
					_log.info("REGISTER: "+ret);
				}
				pm.register((ProcessFactory)ret);
				
				return(ret);
			}
			
			public void removedService(ServiceReference ref, Object service) {
				if (service instanceof ProcessFactory) {
					if (_log.isLoggable(Level.INFO)) {
						_log.info("UNREGISTER: "+service);
					}
					pm.unregister((ProcessFactory)service);
				}
			}
		};

		m_processFactoryTracker.open();
		
		// Check if any process factories already exist
		ServiceReference[] srefs=context.getServiceReferences(ProcessFactory.class.getName(), null);
		if (srefs != null) {
			for (int i=0; i < srefs.length; i++) {
				Object service=context.getService(srefs[i]);
				if (service instanceof ProcessFactory) {
					pm.register((ProcessFactory)service);
				} else {
					_log.severe("Object in not a ProcessFactory");
				}
			}
		}
		
		// Establish listen for the messaging layer
		m_messagingLayerTracker = new ServiceTracker(context,
				MessagingLayer.class.getName(), null) {

			public Object addingService(ServiceReference ref) {
				Object ret=super.addingService(ref);
				
				if (_log.isLoggable(Level.INFO)) {
					_log.info("REGISTER ML: "+ret);
				}
				pm.setMessagingLayer((MessagingLayer)ret);
				
				return(ret);
			}
			
			public void removedService(ServiceReference ref, Object service) {
			}
		};

		m_messagingLayerTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
