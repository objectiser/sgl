/*
 * Copyright 2009-10 www.scribble.org
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
package org.scribble.lang.projection.impl;

import java.util.logging.Logger;

import org.scribble.lang.model.*;
import org.scribble.common.logging.Journal;

/**
 * This class represents the default projection context.
 */
public class DefaultProjectorContext implements ProjectorContext {

	/**
	 * This is the default constructor for the projection context.
	 */
	public DefaultProjectorContext() {
	}
	
	/**
	 * This method projects the supplied model object based on the
	 * specified role.
	 * 
	 * @param model The model object
	 * @param role The role
	 * @param l The model listener
	 * @return The projected model object
	 */
	public ModelObject project(ModelObject model, String role,
						Journal l) {
		ModelObject ret=null;
		
		for (int i=0; model != null && ret == null && i < getRules().size(); i++) {
			if (getRules().get(i).isSupported(model)) {
				ret = getRules().get(i).project(this, model, role,
								l);
			}
		}
				
		return(ret);
	}
		
	/**
	 * This method returns a list of projection rules.
	 * 
	 * @return The list of projection rules
	 */
	public java.util.List<ProjectorRule> getRules() {
		return(m_rules);
	}
	
	/**
	 * This method returns the named state from the current
	 * scope.
	 * 
	 * @param name The state name
	 * @return The state value, or null if not found
	 */
	public Object getState(String name) {
		return(m_scope.getState(name));
	}
	
	/**
	 * This method sets the value associated with the supplied
	 * name in the current state scope.
	 * 
	 * @param name The state name
	 * @param value The state value
	 */
	public void setState(String name, Object value) {
		m_scope.setState(name, value);
	}

	/**
	 * This method pushes the current state onto a stack.
	 */
	public void pushState() {
		m_scope.pushState();
	}
	
	/**
	 * This method pops the current state from the stack.
	 */
	public void popState() {
		m_scope.popState();
	}
		
	/**
	 * This method pushes the current scope onto a stack.
	 */
	public void pushScope() {
		m_scopeStack.add(0, m_scope);
		m_scope = new Scope();
	}
	
	/**
	 * This method pops the current scope from the stack.
	 */
	public void popScope() {
		if (m_scopeStack.size() > 0) {
			m_scope = m_scopeStack.remove(0);
		} else {
			logger.severe("No state entry to pop from stack");
		}
	}
	
	/**
	 * This method determines whether the context is associated
	 * with the outer scope.
	 * 
	 * @return Whether the context is for the outer scope
	 */
	public boolean isOuterScope() {
		return(m_scopeStack.size() < 1);
	}

	private static Logger logger = Logger.getLogger(DefaultProjectorContext.class.getName());
	
	private static java.util.List<ProjectorRule> m_rules=new java.util.Vector<ProjectorRule>();
	private Scope m_scope=new Scope();
	private java.util.List<Scope> m_scopeStack=new java.util.Vector<Scope>();

	static {
		m_rules.add(new ActorExpressionProjectorRule());
		m_rules.add(new ActorListProjectorRule());
		m_rules.add(new ActorParameterProjectorRule());
		m_rules.add(new ActorProjectorRule());
		m_rules.add(new ActorStateProjectorRule());
		m_rules.add(new BlockProjectorRule());
		m_rules.add(new ChoiceProjectorRule());
		m_rules.add(new ConcurrentPathProjectorRule());
		m_rules.add(new ElseProjectorRule());
		m_rules.add(new ElseIfProjectorRule());
		m_rules.add(new ExpressionProjectorRule());
		m_rules.add(new ExprStatementProjectorRule());
		m_rules.add(new IfProjectorRule());
		m_rules.add(new ImportProjectorRule());
		m_rules.add(new InteractionProjectorRule());
		m_rules.add(new LanguageModelProjectorRule());
		m_rules.add(new LangUnitProjectorRule());
		m_rules.add(new NamespaceProjectorRule());
		m_rules.add(new ParProjectorRule());
		m_rules.add(new ProtocolProjectorRule());
		m_rules.add(new RecurCallProjectorRule());
		m_rules.add(new RecurProjectorRule());
		m_rules.add(new RequirementParameterProjectorRule());
		m_rules.add(new RequirementProjectorRule());
		m_rules.add(new StateAccessorProjectorRule());
		m_rules.add(new StateDefinitionProjectorRule());
		m_rules.add(new TypeProjectorRule());
		m_rules.add(new VariableProjectorRule());
		m_rules.add(new WhenProjectorRule());
		m_rules.add(new WhileProjectorRule());
	}
}
