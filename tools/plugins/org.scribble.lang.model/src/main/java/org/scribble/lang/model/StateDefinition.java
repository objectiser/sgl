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

public class StateDefinition extends ModelObject {

	private static final long serialVersionUID = 2081126867669346249L;

	private String m_name=null;
	private Expression m_initializer=null;
	private Actor m_actor=null;
	
	/**
	 * This method returns the actor associated with the state
	 * definition.
	 * 
	 * @return The actor
	 */
	public Actor getActor() {
		return(m_actor);
	}
	
	/**
	 * This method sets the actor associated with the state
	 * definition.
	 * 
	 * @param actor The actor
	 */
	public void setActor(Actor actor) {
		m_actor = actor;
	}
	
	/**
	 * This method returns the fully qualified name associated with the
	 * state definition.
	 * 
	 * @return The name
	 */
	public String getName() {
		return(m_name);
	}
	
	/**
	 * This method sets the fully qualified name of the state definition.
	 * 
	 * @param name The name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * This method returns the initializer associated with the
	 * state definition.
	 * 
	 * @return The initializer
	 */
	public Expression getInitializer() {
		return(m_initializer);
	}
	
	/**
	 * This method sets the initializer associated with the
	 * state definition.
	 * 
	 * @param init The initializer
	 */
	public void setInitializer(Expression init) {
		m_initializer = init;
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		if (getActor() != null) {
			getActor().toText(buf, level);
		}
		
		if (getName() != null) {
			if (getActor() != null) {
				buf.append(':');
			}
			
			buf.append(getName());
			
			if (getInitializer() != null) {
				buf.append('=');
				getInitializer().toText(buf, level);
			}
		}
	}
	
	/**
	 * This method returns the type associated with the state
	 * definition.
	 * 
	 * @return The type
	 */
	public Type getType() {
		Type ret=null;
		
		if (getParent() instanceof Variable) {
			ret = ((Variable)getParent()).getType();
		}
		
		return(ret);
	}
}
