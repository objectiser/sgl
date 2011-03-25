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
package org.scribble.lang.model;

//import java.util.logging.Logger;

/**
 * This is the generic object from which all Scribble model objects
 * are derived.
 */
public abstract class ModelObject implements java.io.Serializable {

	private static final long serialVersionUID = -8915435247669402908L;

	/**
	 * This is the default constructor for the model object.
	 */
	public ModelObject() {
	}
	
	public ModelObject(ModelObject obj) {
		m_properties.putAll(obj.getProperties());
	}
	
	/**
	 * This method returns the parent of this
	 * model object.
	 * 
	 * @return The parent, or null if top model
	 * 					object
	 */
	public ModelObject getParent() {
		return(m_parent);
	}
	
	/**
	 * This method sets the parent model object.
	 * 
	 * @param parent The parent
	 */
	public void setParent(ModelObject parent) {
		m_parent = parent;
	}
	
	/**
	 * This method establishes the necessary information to
	 * indicate that the current model object is derived
	 * from the supplied source model object.
	 * 
	 * @param modelObj The source model object
	 */
	public void derivedFrom(ModelObject modelObj) {
		if (modelObj != null) {
			m_properties = new java.util.HashMap<String,java.io.Serializable>(modelObj.getProperties());
		}
	}
	
	/**
	 * This method returns the properties associated
	 * with this model object.
	 * 
	 * @return The properties
	 */
	public java.util.Map<String,java.io.Serializable> getProperties() {
		return(m_properties);
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public abstract void toText(StringBuffer buf, int level);
	
	protected void indent(StringBuffer buf, int level) {
		for (int i=0; i < level; i++) {
			buf.append('\t');
		}
	}
	
	private ModelObject m_parent=null;
	private java.util.Map<String,java.io.Serializable> m_properties=
				new java.util.HashMap<String, java.io.Serializable>();
}
