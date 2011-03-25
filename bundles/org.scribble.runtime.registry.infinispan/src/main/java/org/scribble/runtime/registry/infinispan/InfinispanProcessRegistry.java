/*
 * Copyright 2010 scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.runtime.registry.infinispan;

import java.util.logging.Level;

import org.scribble.runtime.ProcessFactory;
import org.scribble.runtime.messaging.Endpoint;
import org.scribble.runtime.registry.ProcessRegistry;
import org.infinispan.*;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.manager.*;

/**
 * This class provides the in-memory process registry implementation.
 *
 */
public class InfinispanProcessRegistry implements ProcessRegistry {

	private static final java.util.logging.Logger _log=
		java.util.logging.Logger.getLogger(InfinispanProcessRegistry.class.getName());
	
	private Cache<Object, Object> m_cache=null;
	
	public InfinispanProcessRegistry() {
		try {
			EmbeddedCacheManager manager = new DefaultCacheManager("all.xml");
			m_cache = manager.getCache("distributedCache");
			m_cache.start();
			
		} catch(Exception e) {
			_log.log(Level.SEVERE, "Failed to create cache", e);
		}
	}

	/**
	 * This method registers the process factory, making the
	 * endpoint associated with the factory available to other
	 * processes that wish to communicate with it.
	 * 
	 * @param factory The process factory
	 */
	public void register(ProcessFactory factory) {
		CacheEntry entry=new CacheEntry(factory.getProperties(), factory.getEndpoint());
		
		registerEntry(factory.getName(), entry);
		
		for (String implName : factory.getImplements()) {
			registerEntry(implName, entry);
		}
	}
	
	protected void registerEntry(String name, CacheEntry entry) {
		java.util.Vector<CacheEntry> entries=
			(java.util.Vector<CacheEntry>)m_cache.get(name);
		boolean replace=true;
		
		if (entries == null) {
			entries = new java.util.Vector<CacheEntry>();
			replace = false;
		}
		
		entries.add(entry);
		
System.out.println("REGISTRY name="+name+" already exists? "+m_cache.containsKey(name));		
		if (replace) {
			m_cache.replace(name, entries);
		} else {
			m_cache.put(name, entries);
		}
System.out.println("REGISTRY name="+name+" entry="+entry+" registered="+m_cache.containsKey(name));		
	}
	
	/**
	 * This method unregisters the process factory, indicating
	 * that it is no longer available at this location.
	 * 
	 * @param factory The process factory
	 */
	public void unregister(ProcessFactory factory) {
		unregisterEntry(factory.getName(), factory.getEndpoint());
		
		for (String implName : factory.getImplements()) {
			unregisterEntry(implName, factory.getEndpoint());
		}
	}
	
	protected void unregisterEntry(String name, Endpoint endpoint) {
		java.util.Vector<CacheEntry> entries=
			(java.util.Vector<CacheEntry>)m_cache.get(name);
		
		if (entries != null) {
			for (int i=0; i < entries.size(); i++) {
				CacheEntry ce=entries.get(i);
				
				if (ce.getEndpoint().equals(endpoint)) {
					entries.remove(i);
					break;
				}
			}
			
			if (entries.size() == 0) {
				m_cache.remove(name);
			} else {
				m_cache.replace(name, entries);
			}
		}
System.out.println("REGISTRY name="+name+" endpoint="+endpoint+" registered="+m_cache.containsKey(name));		
	}
	
	/**
	 * This method attempts to find an endpoint associated with the
	 * specified process name and optional properties.
	 * 
	 * @param name The process name
	 * @param props The optional properties
	 * @return The endpoint, or null if not found
	 */
	public Endpoint find(String name, java.util.Properties props) {
		Endpoint ret=null;
		
		// TODO: Ideally need to be able to find local endpoints
		
		java.util.List<CacheEntry> entries=
					(java.util.List<CacheEntry>)m_cache.get(name);
		
//System.err.println("FIND("+Thread.currentThread()+"): name="+name+" entries="+entries);		
		if (entries != null) {
			for (int i=0; ret == null && i < entries.size(); i++) {
				CacheEntry ce=entries.get(i);
				
				if (isMatch(props, ce.getProperties())) {
					ret = ce.getEndpoint();
				}
			}
		}
		
//System.err.println("FIND("+Thread.currentThread()+") name="+name+" ret="+ret);		
		return(ret);
	}
	
	protected boolean isMatch(java.util.Properties wanted, java.util.Properties entry) {
		boolean ret=false;
		
		if (isEmpty(wanted) && isEmpty(entry)) {
			ret = true;
		} else if (!isEmpty(wanted) && !isEmpty(entry) &&
				isSame(wanted, entry)) {
			ret = true;
		}
		
		return(ret);
	}
	
	protected boolean isEmpty(java.util.Properties props) {
		return(props == null || props.size() == 0);
	}
	
	protected boolean isSame(java.util.Properties wanted, java.util.Properties entry) {
		boolean ret=false;
		
		if (wanted.size() == entry.size()) {
			ret = true;
			
			java.util.Iterator<Object> iter=wanted.keySet().iterator();
			
			while (ret && iter.hasNext()) {
				String key=(String)iter.next();
				if (entry.containsKey(key) == false ||
						entry.get(key).equals(wanted.get(key)) == false) {
					ret = false;
				}
			}
		}
		
		return(ret);
	}
	
	public static class CacheEntry implements java.io.Serializable {
		
		private static final long serialVersionUID = 3410704846945838139L;

		private java.util.Properties m_properties=null;
		private Endpoint m_endpoint=null;
		
		public CacheEntry(java.util.Properties props, Endpoint endpoint) {
			m_properties = props;
			m_endpoint = endpoint;
		}
		
		public java.util.Properties getProperties() {
			return(m_properties);
		}
		
		public Endpoint getEndpoint() {
			return(m_endpoint);
		}
	}
}
