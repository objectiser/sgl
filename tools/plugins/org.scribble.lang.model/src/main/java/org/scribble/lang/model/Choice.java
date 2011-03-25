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

/**
 * This class represents the choice activity. This construct is used
 * to describe the alternate paths that may be enacted based on
 * receiving an interaction from an abstract actor.
 *
 */
public class Choice extends Activity {

	private static final long serialVersionUID = -8668539205669692517L;

	private java.util.List<When> m_whens=
		new ContainmentList<When>(this, When.class);
	
	/**
	 * This method returns the list of when blocks.
	 * 
	 * @return The when blocks
	 */
	public java.util.List<When> getWhens() {
		return(m_whens);
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
		return(false);
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		indent(buf, level);
		
		buf.append("choice {\r\n");
		
		for (When when : getWhens()) {
			when.toText(buf, level+1);
		}
		
		indent(buf, level);
		
		buf.append("}\r\n");
	}
	
	/**
	 * This method visits the model object using the supplied
	 * visitor.
	 * 
	 * @param visitor The visitor
	 */
	public void visit(Visitor visitor) {
		visitor.start(this);
		
		for (int i=0; i < getWhens().size(); i++) {
			When when=getWhens().get(i);
			
			when.visit(visitor);
		}
		
		visitor.end(this);
	}
}
