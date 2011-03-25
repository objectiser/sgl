package org.scribble.lang.eclipse.osgi;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.scribble.lang.eclipse.JavaCodeGenerator;
import org.scribble.lang.parser.LanguageParser;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements org.eclipse.ui.IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.scribble.lang.eclipse"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
    private static Logger logger = Logger.getLogger(Activator.class.getName());
	
    private JavaCodeGenerator m_generator=new JavaCodeGenerator();
    
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		ServiceReference sref=context.getServiceReference(LanguageParser.class.getName());
		
		if (sref != null) {
			LanguageParser parser=(LanguageParser)context.getService(sref);
			
			m_generator.setLanguageParser(parser);
		}
		
		initialize();
	}
	
	public void initialize() {
		
		// Initialize the resource change listener
		IResourceChangeListener rcl=
					new IResourceChangeListener() {
			
			public void resourceChanged(IResourceChangeEvent evt) {

				try {
					evt.getDelta().accept(new IResourceDeltaVisitor() {
						
				        public boolean visit(IResourceDelta delta) {
				        	boolean ret=true;
				        	IResource res = delta.getResource();
				        	
							// Determine if the change is relevant
							if (isChangeRelevant(res,
										delta)) {
								
								// Process the resource
								processResource(res);
							}
							
				        	return(ret);
				        }
				 	});
				} catch(Exception e) {
					logger.log(Level.SEVERE,
						"Failed to process resource change event",
						e);
				}
			}
		};
		
		// Register the resource change listener
		ResourcesPlugin.getWorkspace().addResourceChangeListener(rcl,
				IResourceChangeEvent.POST_CHANGE);		

	}

	/**
	 * This method processes the supplied resource.
	 * 
	 * @param res The resource
	 */
	protected void processResource(final IResource res) {
		
		new Thread(new Runnable() {     	
        	public void run() {
				try {
					m_generator.generate(res);
						
				} catch(Exception e) {
					logger.log(Level.SEVERE,
							"Failed to generate resource", e);
				}
        	}
		}).start();
	}
	
	/**
	 * This method determines whether the supplied resource
	 * change event is relevant.
	 * 
	 * @param res The resource
	 * @param deltaFlags The flags
	 * @return Whether the change is relevant
	 */
	protected boolean isChangeRelevant(IResource res, IResourceDelta delta) {
		boolean ret=false;

		// Is the resource a CDL file?
		// Are the changes associated with the contents?
		if (res != null && res.getFileExtension() != null &&
				res.getFileExtension().equals("sl") &&
				(((delta.getFlags() & IResourceDelta.CONTENT) != 0) ||
				delta.getKind() == IResourceDelta.ADDED)) {
			ret = true;
		}

		return(ret);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public void earlyStartup() {
	}

}
