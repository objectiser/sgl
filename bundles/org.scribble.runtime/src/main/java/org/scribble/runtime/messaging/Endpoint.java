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
package org.scribble.runtime.messaging;

/**
 * This class represents the endpoint associated with a process
 * definition.
 *
 */
public class Endpoint implements java.io.Serializable {

	private static final long serialVersionUID = 5095845577853781711L;

	private String m_name=null;
	private java.util.UUID m_id=null;
	private java.util.UUID m_channelId=java.util.UUID.randomUUID();
	
	public Endpoint(String name, java.util.UUID id) {
		m_name = name;
		m_id = id;
	}
	
	public Endpoint(Endpoint endpoint) {
		m_name = endpoint.getName();
		m_id = endpoint.getId();
	}
	
	public String getName() {
		return(m_name);
	}
	
	public java.util.UUID getId() {
		return(m_id);
	}
	
	public java.util.UUID getChannelId() {
		return(m_channelId);
	}
	
	public void setChannelId(java.util.UUID id) {
		m_channelId = id;
	}
	
	public int hashCode() {
		return(m_name.hashCode());
	}
	
	public boolean equals(Object obj) {
		boolean ret=false;
		
		if (obj instanceof Endpoint) {
			ret = ((Endpoint)obj).m_name.equals(m_name) &&
					((Endpoint)obj).m_channelId.equals(m_channelId);
			
			if (ret && (((Endpoint)obj).m_id != null || m_id != null)) {
				
				ret = ((Endpoint)obj).m_id == null || m_id == null ||
						((Endpoint)obj).m_id.equals(m_id);
			}
		}
		
		return(ret);
	}
	
	public String toString() {
		return("Endpoint["+getName()+",id="+getId()+",channel="+getChannelId()+"]");
	}
}
