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

public class ElseIf extends ModelObject {

	private static final long serialVersionUID = 5014192429909643037L;

	private Expression m_expression=null;
	private Block m_block=null;
	
	/**
	 * This method returns the expression.
	 * 
	 * @return The expresson
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
	 * This method returns the block of activities associated
	 * with the definition.
	 * 
	 * @return The block of activities
	 */
	public Block getBlock() {
		
		if (m_block == null) {
			m_block = new Block();
			m_block.setParent(this);
		}
		
		return(m_block);
	}
	
	/**
	 * This method sets the block of activities associated
	 * with the definition.
	 * 
	 * @param block The block of activities
	 */
	public void setBlock(Block block) {
		if (m_block != null) {
			m_block.setParent(null);
		}
		
		m_block = block;
		
		if (m_block != null) {
			m_block.setParent(this);
		}
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		indent(buf, level);
		
		buf.append("} else if (");
		if (getExpression() != null) {
			getExpression().toText(buf, level);
		}
		buf.append(") {\r\n");
		
		if (getBlock() != null) {
			getBlock().toText(buf, level+1);
		}
	}

	public void visit(Visitor visitor) {
		visitor.start(this);
		
		if (m_block != null) {
			m_block.visit(visitor);
		}
		
		visitor.end(this);
	}
}
