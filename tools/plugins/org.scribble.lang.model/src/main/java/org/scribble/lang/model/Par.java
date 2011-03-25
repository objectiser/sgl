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

public class Par extends Activity {

	private static final long serialVersionUID = -849949137726672296L;

	private java.util.List<ConcurrentPath> m_paths=
		new ContainmentList<ConcurrentPath>(this, ConcurrentPath.class);
	
	/**
	 * This method returns the list of blocks.
	 * 
	 * @return The blocks
	 */
	public java.util.List<ConcurrentPath> getPaths() {
		return(m_paths);
	}
	
	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		indent(buf, level);
		
		buf.append("par {\r\n");
		
		for (int i=0; i < getPaths().size(); i++) {
			if (i > 0) {
				indent(buf, level);
				buf.append("} and {\r\n");
			}
			
			getPaths().get(i).toText(buf, level+1);
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
		
		for (int i=0; i < getPaths().size(); i++) {
			ConcurrentPath cp=getPaths().get(i);
			
			cp.visit(visitor);
		}

		visitor.end(this);
	}
}
