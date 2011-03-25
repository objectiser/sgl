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
package org.scribble.runtime.manager.infinispan;

import java.util.logging.Level;

import org.scribble.infinispan.Cacher;
import org.scribble.runtime.Process;
import org.scribble.runtime.manager.AbstractProcessManager;
import org.scribble.runtime.messaging.Endpoint;
import org.infinispan.*;

/**
 * This class provides the in-memory process manager implementation.
 *
 */
public class InfinispanProcessManager extends AbstractProcessManager {

	private static final java.util.logging.Logger _log=
		java.util.logging.Logger.getLogger(InfinispanProcessManager.class.getName());
	
	private Cache<Object, Object> m_cache=null;
	
	private ThreadLocal<Integer> m_txnDepth=new ThreadLocal<Integer>();
	
	public InfinispanProcessManager() {
		m_cache = Cacher.getCache();
		
		/*
		try {
			EmbeddedCacheManager manager = new DefaultCacheManager("/all.xml");
			m_cache = manager.getCache("distributedCache");
			m_cache.start();
			
		} catch(Exception e) {
			_log.log(Level.SEVERE, "Failed to create cache", e);
		}
		*/
	}
	
	/**
	 * This method retrieves the process associated with the supplied
	 * endpoint.
	 * 
	 * @param endpoint The endpoint
	 * @return The process, or null if not found
	 */
	protected Process getProcess(Endpoint endpoint) {
		Process ret=null;
		
		if (startTxn()) {
			try {
//System.err.println("GPB: Thread "+Thread.currentThread()+" BEGIN(get) ep="+endpoint);			
				m_cache.getAdvancedCache().getTransactionManager().begin();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		//m_cache.getAdvancedCache().lock(endpoint.toString());
		
		ret = (Process)m_cache.get(endpoint.toString());
//System.err.println("GPB: Thread "+Thread.currentThread()+" GET key="+endpoint.toString()+" = "+ret);			
		//_log.info("getProcess endpoint="+endpoint+" ret="+ret);	

		return(ret);
	}
	
	protected boolean startTxn() {
		Integer i=m_txnDepth.get();
		if (i == null) {
			i = 0;
		}
		
		i++;
		
		m_txnDepth.set(i);
		
		return(i == 1);
	}
	
	protected boolean endTxn() {
		Integer i=m_txnDepth.get();
		i--;
		
		if (i <= 0) {
			i = null;
		}
		m_txnDepth.set(i);
		
		return(i == null);
	}
	
	/**
	 * This method 'returns' the process to the process
	 * manager with no changes.
	 * 
	 * @param endpoint The endpoint
	 * @param p The process
	 */
	protected void ungetProcess(Endpoint endpoint, Process p) {
		if (endTxn()) {
			try {
//System.err.println("GPB: Thread "+Thread.currentThread()+" ROLLBACK p="+p+" endpoint="+endpoint);			
				m_cache.getAdvancedCache().getTransactionManager().rollback();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method stores the supplied process.
	 * 
	 * @param p The process
	 */
	protected void storeProcess(Process p) {
		if (startTxn()) {
			try {
//System.err.println("GPB: Thread "+Thread.currentThread()+" BEGIN(store) p="+p+" ep="+p.getEndpoint());			
				m_cache.getAdvancedCache().getTransactionManager().begin();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		//m_cache.getAdvancedCache().lock(p.getEndpoint().toString());	

		//_log.info("storeProcess process="+p+" endpoint="+p.getEndpoint());		
		m_cache.put(p.getEndpoint().toString(), p);
//System.err.println("GPB: Thread "+Thread.currentThread()+" STORE key="+p.getEndpoint().toString()+" = "+p);			
	}
	
	/**
	 * This method removes the supplied process.
	 * 
	 * @param p The process to remove
	 */
	protected void removeProcess(Process p) {
		//_log.info("removeProcess process="+p+" endpoint="+p.getEndpoint());		
		m_cache.remove(p.getEndpoint().toString());
//System.err.println("GPB: Thread "+Thread.currentThread()+" REMOVE key="+p.getEndpoint().toString()+" = "+p);			
		
		if (endTxn()) {
			try {
//System.err.println("GPB: Thread "+Thread.currentThread()+" COMMIT(remove) p="+p+" ep="+p.getEndpoint());			
				m_cache.getAdvancedCache().getTransactionManager().commit();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method updates the supplied process.
	 * 
	 * @param p The process to update
	 */
	protected void updateProcess(Process p) {
		//_log.info("updateProcess process="+p+" endpoint="+p.getEndpoint());		
		if (m_cache.replace(p.getEndpoint().toString(), p) == null) {
			_log.severe("Error replacing process associated with endpoint: "+p.getEndpoint());
		}
//System.err.println("GPB: Thread "+Thread.currentThread()+" UPDATE key="+p.getEndpoint().toString()+" = "+p);			
		
		if (endTxn()) {
			try {
//System.err.println("GPB: Thread "+Thread.currentThread()+" COMMIT(update) p="+p+" ep="+p.getEndpoint());			
				m_cache.getAdvancedCache().getTransactionManager().commit();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
