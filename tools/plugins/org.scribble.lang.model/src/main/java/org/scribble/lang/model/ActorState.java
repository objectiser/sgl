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

public class ActorState extends ModelObject {

	private static final long serialVersionUID = -3795687831930795752L;

	private Actor m_actor=null;
	private String m_state=null;
	
	public Actor getActor() {
		return(m_actor);
	}
	
	public void setActor(Actor actor) {
		m_actor = actor;
	}
	
	public String getState() {
		return(m_state);
	}
	
	public void setState(String name) {
		m_state = name;
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		if (getActor() != null) {
			getActor().toText(buf, level);
		}
		
		if (getState() != null) {
			buf.append(':');
			buf.append(getState());
		}
	}
	
	public String toString() {
		String ret="(no actor)";
		
		if (getActor() != null) {
			ret = getActor().getName();
		}
		
		if (getState() != null) {
			ret += ":"+getState();
		}
		
		return(ret);
	}
}
