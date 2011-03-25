package org.scribble.runtime.registry.infinispan.osgi;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.scribble.runtime.registry.ProcessRegistry;
import org.scribble.runtime.registry.infinispan.InfinispanProcessRegistry;

public class Activator implements BundleActivator {

	private static final java.util.logging.Logger _log=
		java.util.logging.Logger.getLogger(Activator.class.getName());
	
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
        Properties props = new Properties();

        // Register the Process Registry
		final InfinispanProcessRegistry pr=new InfinispanProcessRegistry();
		
		context.registerService(ProcessRegistry.class.getName(), 
					pr, props);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
