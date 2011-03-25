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
package org.scribble.infinispan;

import java.util.logging.Level;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class Cacher {

	private static final java.util.logging.Logger _log=
		java.util.logging.Logger.getLogger(Cacher.class.getName());

	public static org.infinispan.Cache<Object, Object> getCache() {
		org.infinispan.Cache<Object, Object> ret=null;
		
		try {
			org.infinispan.executors.DefaultExecutorFactory def=null;
			
			EmbeddedCacheManager manager = new DefaultCacheManager("all.xml");
			ret = manager.getCache("distributedCache");
			ret.start();
			
		} catch(Exception e) {
			_log.log(Level.SEVERE, "Failed to create cache", e);
		}

		return(ret);
	}
}
