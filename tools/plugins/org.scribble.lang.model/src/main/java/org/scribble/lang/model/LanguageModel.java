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

public class LanguageModel extends ModelObject {

	private static final long serialVersionUID = -5558653393942665599L;

	private Namespace m_declaration=null;
	private java.util.List<Import> m_imports=
		new ContainmentList<Import>(this, Import.class);
	private java.util.List<Requirement> m_requirements=
		new ContainmentList<Requirement>(this, Requirement.class);
	private LangUnit m_langUnit=null;
	
	/**
	 * This method returns the namespace.
	 * 
	 * @return The namespace
	 */
	public Namespace getDeclaration() {
		return(m_declaration);
	}
	
	/**
	 * This method sets the namespace.
	 * 
	 * @param ns The namespace
	 */
	public void setDeclaration(Namespace ns) {
		if (m_declaration != null) {
			m_declaration.setParent(null);
		}
		
		m_declaration = ns;
		
		if (m_declaration != null) {
			m_declaration.setParent(this);
		}
	}
	
	/**
	 * This method returns the list of import statements.
	 * 
	 * @return The imports
	 */
	public java.util.List<Import> getImports() {
		return(m_imports);
	}

	/**
	 * This method returns the list of requirements.
	 * 
	 * @return The requirements
	 */
	public java.util.List<Requirement> getRequirements() {
		return(m_requirements);
	}

	/**
	 * This method returns the language unit.
	 * 
	 * @return The language unit
	 */
	public LangUnit getLanguageUnit() {
		return(m_langUnit);
	}
	
	/**
	 * This method sets the language unit.
	 * 
	 * @param unit The language unit
	 */
	public void setLanguageUnit(LangUnit unit) {
		if (m_langUnit != null) {
			m_langUnit.setParent(null);
		}
		
		m_langUnit = unit;
		
		if (m_langUnit != null) {
			m_langUnit.setParent(this);
		}
	}
	
	/**
	 * This method returns the list of local actors associated with the
	 * Scribble Language model.
	 * 
	 * @return The list of local actors
	 */
	public java.util.List<Actor> getLocalActors() {
		final java.util.List<Actor> ret=new java.util.Vector<Actor>();
		
		getLanguageUnit().visit(new DefaultVisitor() {
			
			public void accept(ActorList elem) {
				for (Actor actor : elem.getActors()) {
					if (actor.isLocal() &&
							ret.contains(actor) == false) {
						ret.add(actor);
					}
				}
			}
		});
		
		return(ret);
	}

	/**
	 * This method returns the list of declared actors associated with the
	 * Scribble Language model. This included external and local actors,
	 * but not aliases.
	 * 
	 * @return The list of declared actors
	 */
	public java.util.List<Actor> getDeclaredActors() {
		final java.util.List<Actor> ret=new java.util.Vector<Actor>();
		
		getLanguageUnit().visit(new DefaultVisitor() {
			
			public void accept(ActorList elem) {
				for (Actor actor : elem.getActors()) {
					if (actor.getRepresents() == null &&
							ret.contains(actor) == false) {
						ret.add(actor);
					}
				}
			}
		});
		
		return(ret);
	}

	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		if (getDeclaration() != null) {
			getDeclaration().toText(buf, level);
		}
		
		if (getRequirements().size() > 0) {
			buf.append("\r\n");
			for (Requirement req : getRequirements()) {
				req.toText(buf, level);
			}
		}
		
		if (getImports().size() > 0) {
			buf.append("\r\n");
			for (Import imp : getImports()) {
				imp.toText(buf, level);
			}
		}
		
		if (getLanguageUnit() != null) {
			buf.append("\r\n");
			getLanguageUnit().toText(buf, level);
		}
	}
}
