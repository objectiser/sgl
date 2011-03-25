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

public class ActorParameter extends ModelObject {

	private static final long serialVersionUID = -6434652005989329457L;

	private String m_name=null;
	private Expression m_value=null;
	
	/**
	 * This method returns the name associated with the
	 * actor parameter.
	 * 
	 * @return The name
	 */
	public String getName() {
		return(m_name);
	}
	
	/**
	 * This method sets the name of the actor parameter.
	 * 
	 * @param name The name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * This method returns the value associated with the
	 * actor parameter.
	 * 
	 * @return The value
	 */
	public Expression getValue() {
		return(m_value);
	}
	
	/**
	 * This method sets the value of the actor parameter.
	 * 
	 * @param value The value
	 */
	public void setValue(Expression value) {
		m_value = value;
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		buf.append(getName()+"="+getValue());
	}
	
	public String toString() {
		String ret=getName()+"="+getValue();
		
		return(ret);
	}
}
