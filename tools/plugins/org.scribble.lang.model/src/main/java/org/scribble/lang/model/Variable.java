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

public class Variable extends Activity {

	private static final long serialVersionUID = -3797300725040327998L;

	private Type m_type=null;
	private java.util.List<StateDefinition> m_stateDefinitions=
		new ContainmentList<StateDefinition>(this, StateDefinition.class);
	
	/**
	 * This method returns the type associated with the
	 * actor state.
	 * 
	 * @return The name
	 */
	public Type getType() {
		return(m_type);
	}
	
	/**
	 * This method sets the type of the actor state.
	 * 
	 * @param type The type
	 */
	public void setType(Type type) {
		m_type = type;
	}
	
	/**
	 * This method returns the list of state definitions.
	 * 
	 * @return The state definitions
	 */
	public java.util.List<StateDefinition> getStateDefinitions() {
		return(m_stateDefinitions);
	}	

	/**
	 * This method returns the named state definition.
	 * 
	 * @param name The name
	 * @return The state definition, or null if not found
	 */
	public StateDefinition getStateDefinition(String name) {
		StateDefinition ret=null;
		
		for (StateDefinition sd : getStateDefinitions()) {
			if (sd.getName().equals(name)) {
				ret = sd;
				break;
			}
		}
		
		return(ret);
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		indent(buf, level);
		
		if (getType() != null) {
			getType().toText(buf, level);
			buf.append(" ");
		}
		
		for (int i=0; i < getStateDefinitions().size(); i++) {
			if (i > 0) {
				buf.append(',');
			}
			getStateDefinitions().get(i).toText(buf, level);
		}
		
		buf.append(";\r\n");
	}
	
	/**
	 * This method visits the model object using the supplied
	 * visitor.
	 * 
	 * @param visitor The visitor
	 */
	public void visit(Visitor visitor) {
		visitor.accept(this);
	}
}
