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

public class If extends Activity {

	private static final long serialVersionUID = -827318966318524138L;

	private Actor m_actor=null;
	private Expression m_expression=null;
	private java.util.List<ElseIf> m_elseIfBlocks=
		new ContainmentList<ElseIf>(this, ElseIf.class);
	private Block m_block=null;
	private Else m_elseBlock=null;
	
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
	 * This method returns the list of actors.
	 * 
	 * @return The actors
	 */
	public java.util.List<ElseIf> getElseIfs() {
		return(m_elseIfBlocks);
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
	 * This method returns the else block of activities associated
	 * with the definition.
	 * 
	 * @return The else block of activities
	 */
	public Else getElseBlock() {
		
		if (m_elseBlock == null) {
			m_elseBlock = new Else();
			m_elseBlock.setParent(this);
		}
		
		return(m_elseBlock);
	}
	
	/**
	 * This method sets the else block of activities associated
	 * with the definition.
	 * 
	 * @param block The else block of activities
	 */
	public void setElseBlock(Else block) {
		if (m_elseBlock != null) {
			m_elseBlock.setParent(null);
		}
		
		m_elseBlock = block;
		
		if (m_elseBlock != null) {
			m_elseBlock.setParent(this);
		}
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		indent(buf, level);
		
		buf.append("if (");
		
		if (getActor() != null) {
			getActor().toText(buf, level);
			buf.append(':');
		}
		
		if (getExpression() != null) {
			getExpression().toText(buf, level);
		}
		buf.append(") {\r\n");
		
		if (getBlock() != null) {
			getBlock().toText(buf, level+1);
		}
		
		for (ElseIf ei : getElseIfs()) {
			ei.toText(buf, level);
		}
		
		if (getElseBlock() != null) {
			getElseBlock().toText(buf, level);
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
		
		if (m_block != null) {
			m_block.visit(visitor);
		}
		
		for (int i=0; i < getElseIfs().size(); i++) {
			ElseIf ei=getElseIfs().get(i);
			
			ei.visit(visitor);
		}
		
		if (m_elseBlock != null) {
			m_elseBlock.visit(visitor);
		}
		
		visitor.end(this);
	}
}
