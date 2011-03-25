/*
 * Copyright 2009-10 www.scribble.org
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

/**
 * This class represents the definition of a list of actors.
 * 
 */
public class ActorList extends Activity {

	private static final long serialVersionUID = -7104885105861936032L;

	private java.util.List<Actor> m_actors=
		new ContainmentList<Actor>(this, Actor.class);

	/**
	 * The default constructor.
	 */
	public ActorList() {
	}
	
	/**
	 * This method returns the list of actors.
	 * 
	 * @return The list of actors
	 */
	public java.util.List<Actor> getActors() {
		return(m_actors);
	}
	
	/**
	 * This method returns the actor associated with
	 * the supplied name.
	 * 
	 * @param name The actor name
	 * @return The actor, or null if not found
	 */
	public Actor getActor(String name) {
		Actor ret=null;
		
		for (int i=0; ret == null &&
					i < m_actors.size(); i++) {
			if (m_actors.get(i).getName().equals(name)) {
				ret = m_actors.get(i);
			}
		}
		
		return(ret);
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		indent(buf, level);
		
		buf.append("actor ");
		
		for (int i=0; i < getActors().size(); i++) {
			if (i > 0) {
				buf.append(',');
			}
			getActors().get(i).toText(buf, level);
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
