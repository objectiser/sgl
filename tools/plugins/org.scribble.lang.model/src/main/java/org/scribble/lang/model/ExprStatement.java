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
 * This class represents an expression statement/activity.
 * 
 */
public class ExprStatement extends Activity {

	private static final long serialVersionUID = -995171560330343016L;

	private Actor m_actor=null;
	private Expression m_expression=null;

	/**
	 * The default constructor.
	 */
	public ExprStatement() {
	}
	
	/**
	 * This method returns the actor.
	 * 
	 * @return The actor
	 */
	public Actor getActor() {
		return(m_actor);
	}
	
	/**
	 * This method sets the actor.
	 * 
	 * @param actor The actor
	 */
	public void setActor(Actor actor) {
		m_actor = actor;
	}
	
	/**
	 * This method returns the expression.
	 * 
	 * @return The expression
	 */
	public Expression getExpression() {
		return(m_expression);
	}
	
	/**
	 * This method sets the expression.
	 * 
	 * @param expr The expression
	 */
	public void setExpression(Expression expr) {
		m_expression = expr;
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		indent(buf, level);
		
		if (getActor() != null) {
			getActor().toText(buf, level);
		}
		
		if (getExpression() != null) {
			buf.append(':');
			getExpression().toText(buf, level);
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
	
	public String toString() {
		return(getExpression()==null?null:getExpression().getText());
	}
}
