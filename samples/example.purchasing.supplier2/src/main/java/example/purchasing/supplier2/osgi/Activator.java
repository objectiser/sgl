package example.purchasing.supplier2.osgi;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.scribble.runtime.ProcessFactory;

import example.purchasing.supplier2.PurchasingSupplier2ProcessFactory;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static PurchasingSupplier2ProcessFactory m_processFactory=
					new PurchasingSupplier2ProcessFactory();
	private static ServiceRegistration m_registration=null;

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

        // Register the Process Factory
		m_registration = context.registerService(ProcessFactory.class.getName(), 
				m_processFactory, props);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		
		if (m_registration != null) {
			m_registration.unregister();
		}
	}
}
