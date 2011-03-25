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

public class When extends ModelObject {

	private static final long serialVersionUID = -7822371802647594389L;

	private Interaction m_interaction=null;
	private Block m_block=null;
	
	/**
	 * This method returns the interaction associated with the 'when'.
	 * 
	 * @return The interaction
	 */
	public Interaction getInteraction() {
		return(m_interaction);
	}
	
	/**
	 * This method sets the interaction associated with the 'when'.
	 * 
	 * @param interaction The interaction
	 */
	public void setInteraction(Interaction interaction) {
		if (m_interaction != null) {
			m_interaction.setParent(null);
		}
		
		m_interaction = interaction;
		
		if (m_interaction != null) {
			m_interaction.setParent(this);
		}
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
		
		if (getInteraction() != null) {
			getInteraction().toText(buf, level);
		}
		
		buf.append(" {\r\n");
		
		if (getBlock() != null) {
			getBlock().toText(buf, level+1);
		}
		
		indent(buf, level);
		
		buf.append("}\r\n");
	}
	
	public void visit(Visitor visitor) {
		visitor.start(this);
		
		if (m_block != null) {
			m_block.visit(visitor);
		}
		
		visitor.end(this);
	}
}
