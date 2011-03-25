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
 * This class represents an interaction between two or more
 * actors.
 * 
 */
public class Interaction extends Activity {

	private static final long serialVersionUID = 3896182520382839439L;
	
	private ActorExpression m_fromActor=null;
	private java.util.List<ActorState> m_toActors=
		new ContainmentList<ActorState>(this, ActorState.class);

	/**
	 * The default constructor.
	 */
	public Interaction() {
	}
	
	/**
	 * This method returns the 'from' state.
	 * 
	 * @return The 'from' state
	 */
	public ActorExpression getFromActor() {
		return(m_fromActor);
	}
	
	/**
	 * This method sets the 'from' actor.
	 * 
	 * @param fromActor The 'from' actor
	 */
	public void setFromActor(ActorExpression fromActor) {
		m_fromActor = fromActor;
	}
	
	/**
	 * This method returns the list of 'to' actors.
	 * 
	 * @return The list of 'to' actors
	 */
	public java.util.List<ActorState> getToActors() {
		return(m_toActors);
	}
	
	/**
	 * This method determines whether the activity is a wait
	 * state when considered in the context of the supplied
	 * actor.
	 * 
	 * @param actor The actor
	 * @return Whether the activity is a wait state
	 */
	public boolean isWaitState(Actor actor) {
		boolean ret=true;
		
		if (getFromActor() != null && actor.equals(getFromActor().getActor())) {
			ret = false;
		}
		
		return(ret);
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
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		if (getParent() instanceof Block) {
			indent(buf, level);
		}
		
		buf.append(getFromActor());
		
		buf.append(" -> ");
		
		for (int i=0; i < getToActors().size(); i++) {
			if (i > 0) {
				buf.append(',');
			}
			getToActors().get(i).toText(buf, level);
		}
		
		if (getParent() instanceof Block) {
			buf.append(";\r\n");
		}
	}

	public String toString() {
		return(getFromActor()+" -> "+getToActors());
	}
}
