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

/**
 * This class represents an expression.
 * 
 */
public class Expression extends ModelObject {

	private static final long serialVersionUID = 2798157827905032243L;

	private String m_text=null;
	
	/**
	 * The default constructor.
	 */
	public Expression() {
	}
	
	public String getText() {
		return(m_text);
	}
	
	public void setText(String text) {
		m_text = text;
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		if (getText() != null) {
			buf.append(getText());
		}
	}
	
	public String toString() {
		return(getText());
	}
}
