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

public class Actor extends ModelObject {

	private static final long serialVersionUID = 3331952346211679648L;

	private String m_name=null;
	private Protocol m_protocol=null;
	private String m_represents=null;
	private java.util.List<ActorParameter> m_actorParameters=
		new ContainmentList<ActorParameter>(this, ActorParameter.class);
	
	/**
	 * This method returns the name associated with the
	 * actor.
	 * 
	 * @return The name
	 */
	public String getName() {
		return(m_name);
	}
	
	/**
	 * This method sets the name of the actor.
	 * 
	 * @param name The name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * This method determines whether the actor is local.
	 * 
	 * @return Whether the actor is local
	 */
	public boolean isLocal() {
		return((getImplements() == null ||
				getParameters().size() == 0) &&
				getRepresents() == null);
	}
	
	/**
	 * This method returns the protocol implemented by the
	 * actor.
	 * 
	 * @return The protocol
	 */
	public Protocol getImplements() {
		return(m_protocol);
	}
	
	/**
	 * This method sets the protocol implements by the actor.
	 * 
	 * @param protocol The protocol
	 */
	public void setImplements(Protocol protocol) {
		m_protocol = protocol;
	}
	
	/**
	 * This method sets the local actor represented by this
	 * actor name.
	 * 
	 * @param actorName The actor represented by this actor alias
	 */
	public void setRepresents(String actorName) {
		m_represents = actorName;
	}
	
	/**
	 * This method returns the local actor represented by this
	 * actor alias.
	 * 
	 * @return The represented actor name
	 */
	public String getRepresents() {
		return(m_represents);
	}

	/**
	 * This method returns the list of actor parameters.
	 * 
	 * @return The list of actor parameters
	 */
	public java.util.List<ActorParameter> getParameters() {
		return(m_actorParameters);
	}	
	
	public int hashCode() {
		if (m_name != null) {
			return(m_name.hashCode());
		} 
		return(super.hashCode());
	}
	
	public boolean equals(Object obj) {
		boolean ret=false;
		
		if (obj instanceof Actor) {
			Actor other=(Actor)obj;
			
			if (m_name.equals(other.m_name)) {
				ret = true;
			}
		}
		
		return(ret);
	}

	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		buf.append(getName());
		
		if (getImplements() != null) {
			buf.append(" implements ");
			
			getImplements().toText(buf, level);
			
			if (getParameters().size() > 0) {
				
				buf.append(" with ");
				
				for (int i=0; i < getParameters().size(); i++) {
					
					if (i > 0) {
						buf.append(", ");
					}
					
					getParameters().get(i).toText(buf, level);
				}
			}
		}
		
		if (getRepresents() != null) {
			buf.append(" as ");
			buf.append(getRepresents());
		}
	}
}
