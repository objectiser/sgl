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

public class ActorExpression extends ModelObject {

	private static final long serialVersionUID = -5034517439844340626L;

	private Actor m_actor=null;
	private Expression m_expression=null;
	
	public Actor getActor() {
		return(m_actor);
	}
	
	public void setActor(Actor actor) {
		m_actor = actor;
	}
	
	public Expression getExpression() {
		return(m_expression);
	}
	
	public void setExpression(Expression expr) {
		m_expression = expr;
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		if (getActor() != null) {
			getActor().toText(buf, level);
			buf.append(':');
		}
		
		if (getExpression() != null) {
			getExpression().toText(buf, level);
		}
	}
	
	public String toString() {
		String ret="(no actor)";
		
		if (getActor() != null) {
			ret = getActor().getName();
		}

		if (m_expression != null) {
			ret += ":"+m_expression.getText();
		}
		
		return(ret);
	}
}
