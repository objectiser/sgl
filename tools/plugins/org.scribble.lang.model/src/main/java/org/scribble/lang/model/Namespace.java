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

public class Namespace extends ModelObject {

	private static final long serialVersionUID = 3331952346211679648L;

	private String m_name=null;
	
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
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		indent(buf, level);
		
		buf.append("namespace "+getFullName()+";\r\n");
	}
}
