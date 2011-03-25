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

public class Requirement extends ModelObject {

	public static final String TYPE_MODULE="module";
	
	private static final long serialVersionUID = -7337784096841920564L;

	private String m_type=null;
	private java.util.List<RequirementParameter> m_parameters=
		new ContainmentList<RequirementParameter>(this, RequirementParameter.class);
	
	/**
	 * This method returns the type of the requirement.
	 * 
	 * @return The type
	 */
	public String getType() {
		return(m_type);
	}
	
	/**
	 * This method sets the type of the requirement.
	 * 
	 * @param type The type
	 */
	public void setType(String type) {
		m_type = type;
	}

	/**
	 * This method returns the list of requirement parameters.
	 * 
	 * @return The list of requirement parameters
	 */
	public java.util.List<RequirementParameter> getParameters() {
		return(m_parameters);
	}	
	
	/**
	 * This method returns the parameter with the specified
	 * name.
	 * 
	 * @param name The name
	 * @return The parameter, or null if not found
	 */
	public RequirementParameter getParameter(String name) {
		RequirementParameter ret=null;
		
		for (RequirementParameter req : getParameters()) {
			if (req.getName().equals(name)) {
				ret = req;
				break;
			}
		}
		
		return(ret);
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		indent(buf, level);
		
		buf.append("requires ");
		buf.append(getType());
		
		if (getParameters().size() > 0) {
			buf.append(' ');
			
			for (int i=0; i < getParameters().size(); i++) {
				if (i > 0) {
					buf.append(", ");
				}
				getParameters().get(i).toText(buf, level);
			}
		}
		
		buf.append(";\r\n");
	}
}
