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
package org.scribble.lang.model;

public class Protocol extends ModelObject {

	private static final long serialVersionUID = -6877594485575451946L;

	private String m_name=null;
	private String m_role=null;
	
	/**
	 * This method returns the name associated with the
	 * namespace.
	 * 
	 * @return The name
	 */
	public String getFullName() {
		return(m_name);
	}
	
	/**
	 * This method sets the name of the namespace.
	 * 
	 * @param name The name
	 */
	public void setFullName(String name) {
		m_name = name;
	}
	
	/**
	 * This method returns the scope part of the namespace.
	 * 
	 * @return The namespace scope
	 */
	public String getScope() {
		int pos=m_name.lastIndexOf('.');
		
		if (pos == -1) {
			return(null);
		}
		
		return(m_name.substring(0, pos));
	}
	
	/**
	 * This method returns the local name part of the namespace.
	 * 
	 * @return The namespace name
	 */
	public String getLocalName() {
		int pos=m_name.lastIndexOf('.');
		
		if (pos == -1) {
			return(m_name);
		}
		
		return(m_name.substring(pos+1));
	}
	
	/**
	 * This method returns the role associated with the protocol.
	 * 
	 * @return The role
	 */
	public String getRole() {
		return(m_role);
	}
	
	/**
	 * This method sets the role associated with the protocol.
	 * 
	 * @param role The role
	 */
	public void setRole(String role) {
		m_role = role;
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		if (getFullName() != null) {
			buf.append(getFullName());
		}
		
		if (getRole() != null) {
			buf.append('@');
			buf.append(getRole());
		}
	}
	
	public String toString() {
		return(m_name+"@"+m_role);
	}
}
