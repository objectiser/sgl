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

/**
 * This class represents a group of activities.
 * 
 */
public class Block extends ModelObject {

	private static final long serialVersionUID = -5100912641993617590L;

	private java.util.List<Activity> m_contents=
		new ContainmentList<Activity>(this, Activity.class);

	/**
	 * This method returns the contents associated with
	 * the block.
	 * 
	 * @return The contents
	 */
	public java.util.List<Activity> getContents() {
		return(m_contents);
	}
	
	/**
	 * This method adds an activity to the block.
	 * 
	 * @param act The activity
	 * @return Whether the activity has been added
	 */
	public boolean add(Activity act) {
		return(m_contents.add(act));
	}
	
	/**
	 * This method removes an activity from the block.
	 * 
	 * @param act The activity
	 * @return Whether the activity has been removed
	 */
	public boolean remove(Activity act) {
		return(m_contents.remove(act));
	}
	
	/**
	 * This method returns the number of activities
	 * in the block.
	 * 
	 * @return The number of activities
	 */
	public int size() {
		return(m_contents.size());
	}
	
	/**
	 * This method returns the activity at the specified
	 * index.
	 * 
	 * @param index The index
	 * @return The activity
	 * @throws IndexOutOfBoundsException 
	 */
	public Activity get(int index) throws IndexOutOfBoundsException {
		return(m_contents.get(index));
	}
	
	/**
	 * This method returns the index of the supplied activity.
	 * 
	 * @param act The activity
	 * @return The index, or -1 if the activity is not found
	 */
	public int indexOf(Activity act) {
		return(m_contents.indexOf(act));
	}

	/**
	 * Generate to a text based representation.
	 */
	public void toText(StringBuffer buf, int level) {
		for (Activity act : getContents()) {
			act.toText(buf, level);
		}
	}
	
	public void visit(Visitor visitor) {
		
		visitor.start(this);
		
		for (int i=0; i < m_contents.size(); i++) {
			m_contents.get(i).visit(visitor);
		}
		
		visitor.end(this);
	}
}
