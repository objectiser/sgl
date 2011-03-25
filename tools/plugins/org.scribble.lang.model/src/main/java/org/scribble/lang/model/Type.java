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

public class Type extends ModelObject {

	private static final long serialVersionUID = -3251980677534305835L;

	private String m_name=null;
	private java.util.List<Type> m_types=
		new ContainmentList<Type>(this, Type.class);
	
	/**
	 * This method returns the name associated with the
	 * type.
	 * 
	 * @return The name
	 */
	public String getName() {
		return(m_name);
	}
	
	/**
	 * This method sets the name of the type.
	 * 
	 * @param name The name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * This method returns the fully qualified name,
	 * which may include a package from an associated import
	 * statement.
	 * 
	 * @return The fully qualified name
	 */
	public String getFullyQualifiedName() {
		String ret=m_name;
		
		LanguageModel lm=null;
		ModelObject parent=this;
		
		while (lm == null && (parent = parent.getParent()) != null) {
			if (parent instanceof LanguageModel) {
				lm = (LanguageModel)parent;
			}
		}
		
		if (lm != null) {
			for (Import imp : lm.getImports()) {
				if (imp.getName().endsWith(m_name)) {
					ret = imp.getName();
					break;
				}
			}
		}
		
		return(ret);
	}
	
	/**
	 * This method returns the list of types.
	 * 
	 * @return The types
	 */
	public java.util.List<Type> getTypes() {
		return(m_types);
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		buf.append(getName());
		
		if (getTypes().size() > 0) {
			buf.append('<');
			
			for (int i=0; i < getTypes().size(); i++) {
				if (i > 0) {
					buf.append(',');
				}
				getTypes().get(i).toText(buf, level);
			}
			
			buf.append('>');
		}
	}
	
	public Class<?> toClass() {
		Class<?> ret=null;
		
		// TODO: Need to deal with fully qualified type name
		try {
			ret = Class.forName(getName());
		} catch(Exception e) {
		}
		
		return(ret);
	}

	public String toString() {
		String ret=getName();
		
		if (getTypes().size() > 0) {
			ret += "<";
			
			for (int i=0; i < getTypes().size(); i++) {
				if (i > 0) {
					ret += ",";
				}
				ret += getTypes().get(i).toString();
			}
			
			ret += ">";
		}
		
		return(ret);
	}
}
